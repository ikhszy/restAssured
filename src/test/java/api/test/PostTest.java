package api.test;

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
		
		if(response.getStatusCode() == 200
				&& postId == this.pPojo.getId()) {
			Assert.assertTrue(true);
			System.out.println("\nUser Detail: " + response.asPrettyString());
		} else if(response.getStatusCode() != 200) {
			Assert.fail("Status code = " + response.getStatusCode());
		} else {
			Assert.fail(response.getBody().toString());
		}
	}
	
	@Test(priority=3, description="PUT post by Id")
	public void PST003() {
		
		pPojo.setTitle(faker.book().title());
		pPojo.setBody(faker.shakespeare().romeoAndJulietQuote());
		
		Response response = Post_endpoints.postPutById(pPojo, this.pPojo.getId());
		JsonPath jp = response.jsonPath();
		
		int postId = jp.getInt("id");
		
		if(response.getStatusCode() == 200
				&& postId == this.pPojo.getId()) {
			Assert.assertTrue(true);
			System.out.println("\nUser Detail: " + response.asPrettyString());
		} else if(response.getStatusCode() != 200) {
			Assert.fail("Status code = " + response.getStatusCode());
		} else {
			Assert.fail(response.getBody().toString());
		}
	}
	
	@Test(priority=4, description="DELETE post by Id")
	public void PST004() {
		
		Response response = Post_endpoints.postDeleteById(this.pPojo.getId());
		
		if(response.getStatusCode() == 204) {
			Assert.assertTrue(true);
		} else{
			Assert.fail(response.then().log().all().toString());
		}
	}
}
