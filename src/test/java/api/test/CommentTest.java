package api.test;

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
	
	@Test(priority = 1)
	public void CMT001() {
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		
		int post_id = jp.get("post_id");
		
		if(resp.getStatusCode() == 201 && post_id == commentPayload.getPost_id()) {
			Assert.assertTrue(true);
			System.out.println("Success with post_id: " + post_id);
			commentPayload.setId(jp.getInt("id"));
		} else {
			Assert.fail(resp.asPrettyString());
		}
	}
	
	@Test(priority = 2)
	public void CMT002() {
		
		Response resp = Comment_endpoints.getCommentById(this.commentPayload.getId());
		
		JsonPath jp = resp.jsonPath();
		
		int cmtId = jp.get("id");
		
		if(resp.getStatusCode() == 200 && cmtId == this.commentPayload.getId()) {
			Assert.assertTrue(true);
			System.out.println("Success with this as response: \n" + resp.getBody().asPrettyString());
		} else {
			Assert.fail(resp.getBody().asPrettyString());
		}
	}
	
	@Test(priority = 3)
	public void CMT003() {
		
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
	
	@Test(priority = 4)
	public void CMT004() {
		
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
}
