package api.test;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import api.endpoints.Post_endpoints;
import api.endpoints.User_endpoints;
import api.payload.PostPojo;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PostTest {
	
	Faker faker;
	PostPojo pPojo;
	public Logger logg;
	
	
	@BeforeClass
	public void setup() {
		faker = new Faker();
		pPojo = new PostPojo();
		
		// get userId first
		Response response = User_endpoints.getusers_forOtherep();
		
		JsonPath jp = response.jsonPath();
		
		int usrId = jp.getInt("[0].id");
		pPojo.setUser_id(usrId);
		
		System.out.println(usrId);
		
		pPojo.setBody(faker.shakespeare().kingRichardIIIQuote().toLowerCase().trim());
		pPojo.setTitle(faker.book().title().toLowerCase().trim());
		
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
	
	@Test(priority=1, description="POST on post")
	public void PST001() {
		
		Response response = Post_endpoints.postPost(pPojo);
		JsonPath jp = response.jsonPath();
		
		if(response.getStatusCode() == 201) {
			Assert.assertTrue(true);
		} else {
			Assert.fail("Status code = " + response.getStatusCode());
		}
		
		pPojo.setId(jp.getInt("id"));
	}
	
	@Test(priority=2, description="GET post by ID")
	public void PST002() {
		
		Response response = Post_endpoints.postGetById(this.pPojo.getId());
		JsonPath jp = response.jsonPath();
		
		int postId = jp.getInt("id");
		
		if(response.getStatusCode() != 200) {
			Assert.fail("Status code is wrong: " + response.getStatusCode());
		} else if(postId != this.pPojo.getId()) {
			Assert.fail("Post ID is wrong: " + postId);
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority=3, description="PUT post by Id")
	public void PST003() {
		
		pPojo.setTitle(faker.book().title());
		pPojo.setBody(faker.shakespeare().romeoAndJulietQuote());
		
		Response response = Post_endpoints.postPutById(pPojo, this.pPojo.getId());
		JsonPath jp = response.jsonPath();
		
		int postId = jp.getInt("id");
		String checker;
		checker = jp.get("title").toString();
		
		if(response.getStatusCode() != 200) {
			Assert.fail("Status code is wrong: " + response.getStatusCode());
		} else if(!checker.equals(this.pPojo.getTitle())) {
			Assert.fail(response.getBody().asPrettyString());
		} else if(postId != this.pPojo.getId()) {
			Assert.fail("Post ID is wrong: " + postId);
		} else {
			Assert.assertTrue(true);
			System.out.println("\nPUT detail: " + response.asPrettyString());
		}
	}
	
	
	@Test(priority=4, description="Get first 10 post and return the title")
	public void PST004() {
		String compare = this.pPojo.getTitle();
		List<PostPojo> response = Post_endpoints.getAllPost();
		int listCount = response.size();
		
		System.out.println("total entries: " + listCount);
		
		for(int i = 0; i < listCount; i++) {
			if(response.get(i).getTitle().toString().equals(compare)) {
				Assert.assertTrue(true);
				System.out.println("FOUND IT: " + response.get(i).getTitle().toString());
			} else {
				System.out.println(response.get(i).getTitle());
			}
		}
	}
	
	@Test(priority=5, description="Search post by user_id")
	public void PST005() {
		List<PostPojo> response = Post_endpoints.getPostOneParam("user_id", this.pPojo.getUser_id());
		int listCount = response.size();
		
		System.out.println("total data: " + listCount);
		
		for(PostPojo uId : response) {
			System.out.println("found user_id: " + uId.getUser_id() + " \ntitle: " + uId.getTitle());
			System.out.println();
		}
	}
	
	@Test(priority=100, description="DELETE post by Id")
	public void PST006() {
		
		Response response = Post_endpoints.postDeleteById(this.pPojo.getId());
		
		if(response.getStatusCode() == 204) {
			Assert.assertTrue(true);
		} else {
			Assert.fail(response.then().log().all().toString());
		}
	}
	
	@Test(priority=8, description="Negative on post with user id 0")
	public void PST101() {
		
		pPojo.setUser_id(0);
		
		Response response = Post_endpoints.postPost(pPojo);
		JsonPath jp = response.jsonPath();
		
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(response.getStatusCode() != 422) {
			Assert.fail("wrong status code");
		} else if(!valField.equals("user")) {
			Assert.fail("field is wrong");
		} else if(!valMessage.equals("must exist")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority=6, description="Negative on post with empty title")
	public void PST102() {
		
		pPojo.setTitle("");
		
		Response response = Post_endpoints.postPost(pPojo);
		JsonPath jp = response.jsonPath();
		
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(response.getStatusCode() != 422) {
			Assert.fail("wrong status code");
		} else if(!valField.equals("title")) {
			Assert.fail("field is wrong");
		} else if(!valMessage.equals("can't be blank")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority=7, description="Negative on post with empty body")
	public void PST103() {
		
		pPojo.setTitle(faker.book().title());
		pPojo.setBody("");
		
		Response response = Post_endpoints.postPost(pPojo);
		JsonPath jp = response.jsonPath();
		
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(response.getStatusCode() != 422) {
			Assert.fail("wrong status code");
		} else if(!valField.equals("body")) {
			Assert.fail("field is wrong");
		} else if(!valMessage.equals("can't be blank")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
}
