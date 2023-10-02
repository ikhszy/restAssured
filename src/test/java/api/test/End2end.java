package api.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import api.endpoints.Comment_endpoints;
import api.endpoints.Post_endpoints;
import api.endpoints.User_endpoints;
import api.payload.CommentsPojo;
import api.payload.PostPojo;
import api.payload.UserPojo;
import api.utilities.DataProviders;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class End2end {
	
	UserPojo userPayload;
	PostPojo postPayload;
	CommentsPojo commentPayload;
	Faker faker;
	
	@BeforeClass
	public void setup() {
		faker = new Faker();
		userPayload = new UserPojo();
		postPayload = new PostPojo();
		commentPayload = new CommentsPojo();
		
		// initial user data
		userPayload.setName(faker.name().fullName());
		userPayload.setEmail(faker.internet().emailAddress());
		userPayload.setGender("male");
		userPayload.setStatus("Active");
		
		// initial post data
		postPayload.setBody(faker.shakespeare().kingRichardIIIQuote().toLowerCase().trim());
		postPayload.setTitle(faker.book().title().toLowerCase().trim());
		
		//initial comment data
		commentPayload.setName(faker.name().fullName());
		commentPayload.setEmail(faker.internet().emailAddress());
		commentPayload.setBody(faker.lorem().sentence());
		
	}
	
	@Test(priority=1, description="Post new user") 
	public void e2e001() {
		
		Response response = User_endpoints.postUser(userPayload);
		
		JsonPath jp = response.jsonPath();
		int usrId = Integer.parseInt(jp.getString("id"));
		
		userPayload.setId(usrId);
		
		if(response.getStatusCode() == 201) {
			Assert.assertTrue(true);
		} else {
			Assert.fail("Status code = " + response.getStatusCode());
		}
	}
	
	@Test(priority=2, description="POST on post")
	public void e2e002() {
		
		postPayload.setUser_id(userPayload.getId());
		
		Response response = Post_endpoints.postPost(postPayload);
		JsonPath jp = response.jsonPath();
		
		if(response.getStatusCode() == 201) {
			Assert.assertTrue(true);
		} else {
			Assert.fail("Status code = " + response.getStatusCode());
		}
		
		postPayload.setId(jp.getInt("id"));
	}
	
	@Test(priority = 3, description="POST new comment")
	public void e2e003() {
		
		commentPayload.setPost_id(postPayload.getId());
		
		Response resp = Comment_endpoints.postComment(commentPayload);
		JsonPath jp = resp.jsonPath();
		
		int post_id = this.postPayload.getId();
		
		if(resp.getStatusCode() == 201 && post_id == commentPayload.getPost_id()) {
			Assert.assertTrue(true);
			System.out.println("Success with post_id: " + post_id);
			commentPayload.setId(jp.getInt("id"));
		} else {
			Assert.fail(resp.asPrettyString());
		}
	}
	
	@Test(priority = 4, description="PUT new comment and change the name, email and body")
	public void e2e004() {
		
		//set new text
		faker = new Faker();
		commentPayload.setName(faker.name().fullName());
		commentPayload.setEmail(faker.internet().emailAddress());
		commentPayload.setBody(faker.lorem().sentence());
		
		Response resp = Comment_endpoints.putCommentById(commentPayload, this.commentPayload.getId());
		
		JsonPath jp = resp.jsonPath();
		
		int cmtId = jp.get("id");
		
		if(resp.getStatusCode() == 200 && cmtId == this.commentPayload.getId()) {
			Assert.assertTrue(true);
			System.out.println("Success with this as response: \n" + resp.getBody().asPrettyString());
		} else {
			Assert.fail(resp.getBody().asPrettyString());
		}
	}
	
	@Test(priority = 5, description="DELETE the session comment")
	public void e2e005() {
		
		int cmtId = this.commentPayload.getId();
		
		Response delResp = Comment_endpoints.deleteCommentById(cmtId);
		Response getResp = Comment_endpoints.getCommentById(cmtId);
		
		if(delResp.getStatusCode() == 204 && getResp.getStatusCode() == 404) {
			Assert.assertTrue(true);
			System.out.println("comment Id: " + this.commentPayload.getId() + " successfully deleted");
		} else {
			Assert.fail(String.valueOf(getResp.getStatusCode()));
		}
	}
	
	@Test(priority=6, description="PUT post by Id")
	public void e2e006() {
		
		postPayload.setTitle(faker.book().title());
		postPayload.setBody(faker.shakespeare().romeoAndJulietQuote());
		
		Response response = Post_endpoints.postPutById(postPayload, this.postPayload.getId());
		JsonPath jp = response.jsonPath();
		
		int postId = jp.getInt("id");
		
		if(response.getStatusCode() == 200
				&& postId == this.postPayload.getId()) {
			Assert.assertTrue(true);
			System.out.println("\nPUT detail: " + response.asPrettyString());
		} else if(response.getStatusCode() != 200) {
			Assert.fail("Status code = " + response.getStatusCode());
		} else {
			Assert.fail(response.getBody().toString());
		}
	}
	
	@Test(priority=7, description="DELETE post by Id")
	public void e2e007() {
		
		Response response = Post_endpoints.postDeleteById(this.postPayload.getId());
		
		if(response.getStatusCode() == 204) {
			Assert.assertTrue(true);
		} else{
			Assert.fail(response.then().log().all().toString());
		}
	}
	
	@Test(priority=8, description="PUT user using Data Driven Method", dataProvider = "data", dataProviderClass = DataProviders.class)
	public void e2e008(String name, String email, String gender, String status) {
		userPayload.setName(name);
		userPayload.setEmail(email);
		userPayload.setGender(gender);
		userPayload.setStatus(status);
		
		Response response = User_endpoints.putUser(this.userPayload.getId(), userPayload);
		
		JsonPath jp = response.jsonPath();
		
		int usrId = jp.getInt("id");
		String usrName = jp.getString("name");
		String usrEmail = jp.getString("email");
		
		if(
			response.getStatusCode() == 200 &&
			usrId == this.userPayload.getId() &&
			usrName != this.userPayload.getName() &&
			usrEmail != this.userPayload.getEmail()) {
			Assert.assertTrue(true);
			System.out.println("New entry: \n" + response.asPrettyString() + "\n");
				} else if(response.getStatusCode() != 200) {
					Assert.fail("status code: " + response.getStatusCode());
				} else if(usrId != this.userPayload.getId()) {
					Assert.fail("id changed to: " + usrId);
				} else if(usrName == this.userPayload.getName()) {
					Assert.fail("name changed to: " + usrName);
				} else if(usrEmail == this.userPayload.getEmail()) {
					Assert.fail("email changed to: " + usrEmail);
				} else {
					Assert.fail("check response:\n" + response.asPrettyString());
				}
	}
	
	@Test(priority=9, description="Delete user")
	public void e2e009() {
		Response response = User_endpoints.deleteUser(this.userPayload.getId());
		
		if(response.getStatusCode() == 204) {
			Assert.assertTrue(true, "status code: " + response.getStatusCode());
			System.out.println("Successfully deleted " + this.userPayload.getId() + "\n");
		} else {
			Assert.fail("status code: " + response.getStatusCode());
		}
	}
}
