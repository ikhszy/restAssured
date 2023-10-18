package api.test;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import com.github.javafaker.Faker;

import api.endpoints.Comment_endpoints;
import api.endpoints.Post_endpoints;
import api.payload.CommentsPojo;

public class CommentTest {
	Faker faker;
	CommentsPojo commentPayload;
	public Logger logg;
	
	
	@BeforeClass
	public void setup() {
		faker = new Faker();
		commentPayload = new CommentsPojo();
		commentPayload.setName(faker.name().fullName());
		commentPayload.setEmail(faker.internet().emailAddress());
		commentPayload.setBody(faker.lorem().sentence());
		
		// getting post_id
		Response response = Post_endpoints.getPostIdForOthers();
		JsonPath jp = response.jsonPath();
		int postId = jp.getInt("[0].id");
		commentPayload.setPost_id(postId);
		
	}
	
	@Test(priority = 1, description="POST new comment")
	public void CMT001() {
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		
		int post_id = jp.get("post_id");
		
		if(resp.getStatusCode() != 201) {
			Assert.fail("Status code = " + resp.getStatusCode());
		} else if(post_id != commentPayload.getPost_id()) {
			Assert.fail("wrong id: " + post_id);
		} else {
			Assert.assertTrue(true);
			commentPayload.setId(jp.getInt("id"));
		}
	}
	
	@Test(priority = 2, description="GET new comment by id")
	public void CMT002() {
		
		Response resp = Comment_endpoints.getCommentById(this.commentPayload.getId());
		
		JsonPath jp = resp.jsonPath();
		
		int cmtId = jp.getInt("id");
		
		if(resp.getStatusCode() != 200) {
			Assert.fail("Status code = " + resp.getStatusCode());
		} else if(cmtId != commentPayload.getId()) {
			Assert.fail("wrong id: " + cmtId);
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority = 3, description="PUT new comment and change the name, email and body")
	public void CMT003() {
		
		//set new text
		faker = new Faker();
		commentPayload.setName(faker.name().fullName());
		commentPayload.setEmail(faker.internet().emailAddress());
		commentPayload.setBody(faker.lorem().sentence());
		
		Response resp = Comment_endpoints.putCommentById(commentPayload, this.commentPayload.getId());
		
		JsonPath jp = resp.jsonPath();
		
		int cmtId = jp.getInt("id");
		
		if(resp.getStatusCode() != 200) {
			Assert.fail("Status code = " + resp.getStatusCode());
		} else if(cmtId != this.commentPayload.getId()) {
			Assert.fail("wrong id: " + cmtId);
		} else if(!resp.getBody().asPrettyString().contains(this.commentPayload.getName())) {
			Assert.fail("Incorrect name = " + jp.get("name"));
		} else if(!resp.getBody().asPrettyString().contains(this.commentPayload.getEmail())) {
			Assert.fail("Incorrect name = " + jp.get("email"));
		} else if(!resp.getBody().asPrettyString().contains(this.commentPayload.getBody())) {
			Assert.fail("Incorrect name = " + jp.get("body"));
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority = 100, description="DELETE the session comment")
	public void CMT004() {
		
		int cmtId = this.commentPayload.getId();
		
		Response delResp = Comment_endpoints.deleteCommentById(cmtId);
		Response getResp = Comment_endpoints.getCommentById(cmtId);
		
		if(delResp.getStatusCode() != 204) {
			Assert.fail("delete got wrong status code: " + delResp.getStatusCode());
		} else if(getResp.getStatusCode() != 404) {
			Assert.fail("get got wrong status code: " + delResp.getStatusCode());
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority=4, description="Search comment by id")
	public void CMT005() {
		List<CommentsPojo> response = Comment_endpoints.searchCommentsWithInt("id", this.commentPayload.getId());
		int listCount = response.size();
		
		System.out.println("total data: " + listCount);
		
		for(CommentsPojo id : response) {
			System.out.println("found comment with id: " + id.getId() + " \ntitle: " + id.getBody());
		}
	}
	
	@Test(priority = 5, description="Negative post comment with empty name")
	public void CMT101() {
		commentPayload.setName("");
		
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(resp.getStatusCode() != 422) {
			Assert.fail("wrong status code");
		} else if(!valField.equals("name")) {
			Assert.fail("wrong field");
		} else if(!valMessage.equals("can't be blank")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority = 6, description="Negative post comment with empty email")
	public void CMT102() {
		commentPayload.setName(faker.name().firstName());
		commentPayload.setEmail("");
		
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(resp.getStatusCode() != 422) {
			Assert.fail("wrong status code");
		} else if(!valField.equals("email")) {
			Assert.fail("wrong field");
		} else if(!valMessage.equals("can't be blank, is invalid")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority = 7, description="Negative post comment with invalid email")
	public void CMT103() {
		commentPayload.setEmail(faker.pokemon().name());
		
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(resp.getStatusCode() != 422) {
			Assert.fail("wrong status code");
		} else if(!valField.equals("email")) {
			Assert.fail("wrong field");
		} else if(!valMessage.equals("is invalid")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority = 8, description="Negative post comment with empty body")
	public void CMT104() {
		commentPayload.setEmail(faker.internet().emailAddress());
		commentPayload.setBody("");
		
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(resp.getStatusCode() != 422) {
			Assert.fail("wrong status code");
		} else if(!valField.equals("body")) {
			Assert.fail("wrong field");
		} else if(!valMessage.equals("can't be blank")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority = 9, description="Negative post comment with post id not found")
	public void CMT105() {
		commentPayload.setBody(faker.lorem().sentence());
		commentPayload.setPost_id(0);
		
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		String valField = jp.get("[0].field");
		String valMessage = jp.get("[0].message");
		
		if(resp.getStatusCode() != 422) {
			System.out.println(resp.getBody().asPrettyString());
			Assert.fail("wrong status code");
		} else if(!valField.equals("post")) {
			Assert.fail("wrong field");
		} else if(!valMessage.equals("must exist")) {
			Assert.fail("incorrect message");
		} else {
			Assert.assertTrue(true);
		}
	}
}
