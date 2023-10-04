package api.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;
import api.endpoints.User_endpoints;
import api.payload.UserPojo;
import api.utilities.DataProviders;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;
//import org.json.JSONObject;
//import org.json.JSONArray;

//import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class UserTest {
	
	Faker faker;
	UserPojo userPayload;
	public Logger logg;
	
	
	@BeforeClass
	public void setup() {
		faker = new Faker();
		userPayload = new UserPojo();
		
		userPayload.setName(faker.name().fullName());
		userPayload.setEmail(faker.internet().emailAddress());
		userPayload.setGender("male");
		userPayload.setStatus("Active");
		
	}
	
	@Test(priority=1, description="Post new user") 
	public void USR001() {
		
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
	
	@Test(priority=2, description="Get user by ID")
	public void USR002() {
//		String uId = Integer.toString(this.userPayload.getId());
		Response response = User_endpoints.getUser(this.userPayload.getId());
		
		JsonPath jp = response.jsonPath();
		
		int usrId = jp.getInt("id");
		
		if(response.getStatusCode() != 200) {
			Assert.fail("Status code = " + response.getStatusCode());
		} else if(usrId != this.userPayload.getId()) {
			Assert.fail("user id is wrong = " + usrId);
		} else {
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority=3, description="PUT user using Data Driven Method", dataProvider = "data", dataProviderClass = DataProviders.class)
	public void USR003(String name, String email, String gender, String status) {
		userPayload.setName(name);
		userPayload.setEmail(email);
		userPayload.setGender(gender);
		userPayload.setStatus(status);
		
		Response response = User_endpoints.putUser(this.userPayload.getId(), userPayload);
		
		JsonPath jp = response.jsonPath();
		
		int usrId = jp.getInt("id");
		String usrName = jp.getString("name");
		String usrEmail = jp.getString("email");
		
		if(response.getStatusCode() != 200) {
			Assert.fail("status code: " + response.getStatusCode());
		} else if(usrId != this.userPayload.getId()) {
			Assert.fail("id changed to: " + usrId);
		} else if(usrName == this.userPayload.getName()) {
			Assert.fail("name changed to: " + usrName);
		} else if(usrEmail == this.userPayload.getEmail()) {
			Assert.fail("email changed to: " + usrEmail);
		} else {
			Assert.assertTrue(true);
			System.out.println("New entry: \n" + response.asPrettyString() + "\n");
		}
	}
	
	@Test(priority=4, description="Get all user and return the name")
	public void USR005() {
		String compare = this.userPayload.getName();
		List<UserPojo> response = User_endpoints.getAllUser();
		int listCount = response.size();
		
		System.out.println("total entries: " + listCount);
		
		for(UserPojo Name : response) {
			if(Name.getName().contains(compare)) {
				System.out.println("found user: " + Name.getName());
			} else {
				System.out.println(Name.getName());
			}
		}
	}
	
	@Test(priority=5, description="Get all user by status limit to 20 data")
	public void USR006() {
		String compare = this.userPayload.getStatus();
		List<UserPojo> response = User_endpoints.getUserTwoParam("status", this.userPayload.getStatus(), "per_page", "100");
		int listCount = response.size();
		
		System.out.println("total user with status as " + this.userPayload.getStatus() + " : " + listCount);
		
		for(UserPojo status : response) {
			if(status.getStatus().contains(compare)) {
				Assert.assertTrue(true);
			} else {
				Assert.fail("fail for status: " + compare);
			}
			System.out.println("user: " + status.getName() + " Success for status: " + compare);
		}
	}
	
	@Test(priority=6, description="Search user by name")
	public void USR007() {
		List<UserPojo> response = User_endpoints.getUserOneParam("name", this.userPayload.getName());
		int listCount = response.size();
		
		System.out.println("total user with status as " + this.userPayload.getStatus() + " : " + listCount);
		
		for(UserPojo status : response) {
			if(status.getName() == "") {
				Assert.fail();
				System.out.println("Name not found");
			} else {
				Assert.assertTrue(true);
				System.out.println("found similar user: " + status.getName());
			}
		}
	}
	
	@Test(priority=7, description="Delete user")
	public void USR004() {
		Response response = User_endpoints.deleteUser(this.userPayload.getId());
		
		if(response.getStatusCode() == 204) {
			Assert.assertTrue(true, "status code: " + response.getStatusCode());
			System.out.println("Successfully deleted " + this.userPayload.getId() + "\n");
		} else {
			Assert.fail("status code: " + response.getStatusCode());
		}
	}
}
