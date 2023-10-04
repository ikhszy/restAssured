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
	
	@Test(priority = 5, description="DELETE the session comment")
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
	
	@Test(priority=4, description="Search comment by id")
	public void CMT005() {
		List<CommentsPojo> response = Comment_endpoints.searchCommentsWithInt("id", this.commentPayload.getId());
		int listCount = response.size();
		
		System.out.println("total data: " + listCount);
		
		for(CommentsPojo id : response) {
			System.out.println("found comment with id: " + id.getId() + " \ntitle: " + id.getBody());
		}
	}
}
