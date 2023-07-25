package api.test;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import com.github.javafaker.Faker;

import api.endpoints.Comment_endpoints;
import api.payload.CommentsPojo;

public class CommentTest {
	Faker faker;
	CommentsPojo commentPayload;
	public Logger logg;
	
	
	@BeforeClass
	public void setup() {
		faker = new Faker();
		commentPayload = new CommentsPojo();
		
		commentPayload.setPost_id(51937);
		commentPayload.setName(faker.name().fullName());
		commentPayload.setEmail(faker.internet().emailAddress());
		commentPayload.setBody(faker.lorem().sentence());
	}
	
	@Test
	public void CMT001() {
		Response resp = Comment_endpoints.postComment(commentPayload);
		
		JsonPath jp = resp.jsonPath();
		
		int post_id = jp.get("post_id");
		
		if(resp.getStatusCode() == 201) {
			Assert.assertTrue(true);
			System.out.println("Success with post_id: " + post_id);
			System.out.println(jp.toString());
		} else {
			Assert.fail(resp.asPrettyString());
		}
	}
}
