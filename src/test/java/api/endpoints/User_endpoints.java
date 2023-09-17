package api.endpoints;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import api.payload.UserPojo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class User_endpoints {
	
	static ResourceBundle getURL() {
		ResourceBundle config = ResourceBundle.getBundle("config");
		return config;
	}
	
	public static Response postUser (UserPojo payload) {
		
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Posting User....\n");
		
		Response res = given()
			.auth().oauth2(userToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload)
		.when()
			.post(url);
		
		return res;
	}
	
	public static Response getUser (int id) {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Get User by ID ....\n");
		
		Response res = given()
			.auth().oauth2(userToken)
		.when()
			.get(url + "/" + id);
		
		return res;
	}
	
	public static Response getusers_forOtherep() {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Get User by ID ....\n");
		
		Response res = given()
			.auth().oauth2(userToken)
		.when()
			.get(url);
		
		return res;
	}
	
	public static List<UserPojo> getUserOneParam (String param, String value) {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Filter user by " + param +" ....\n");
		
		return Arrays.asList(given()
			.auth().oauth2(userToken)
			.and()
			.queryParam(param, value)
		.when()
			.get(url)
		.then()
			.extract()
			.response()
			.body()
			.as(UserPojo[].class));
	}
	
	public static List<UserPojo> getUserTwoParam (String param1, String value1, String param2, String value2) {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Filter user by " + param1 + " and " + param2 +" ....\n");
		
		return Arrays.asList(given()
			.auth().oauth2(userToken)
			.and()
			.queryParam(param1, value1)
			.and()
			.queryParam(param2, value2)
		.when()
			.get(url)
		.then()
			.extract()
			.response()
			.body()
			.as(UserPojo[].class));
	}
	
	public static List<UserPojo> getUserByName (String name) {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Get User by ID ....\n");
		
		return Arrays.asList(given()
			.auth().oauth2(userToken)
			.and()
			.queryParam("name", name)
		.when()
			.get(url)
		.then()
			.extract()
			.response()
			.body()
			.as(UserPojo[].class));
	}
	
	public static List<UserPojo> getAllUser() {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Get all user ....\n");
		
		return Arrays.asList(given()
				.auth().oauth2(userToken)
				.when()
				.get(url)
				.then()
				.extract()
				.response()
				.body()
				.as(UserPojo[].class));
	}
	
	public static Response putUser (int id, UserPojo payload) {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Updating a user ....\n");
		
		Response res = given()
			.auth().oauth2(userToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload)
		.when()
			.put(url + "/" + id);
		
		return res;
	}
	
	public static Response deleteUser (int id) {
		String url = getURL().getString("usersEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Deleting a user ....\n");
		
		Response res = given()
			.auth().oauth2(userToken)
			.when()
			.delete(url + "/" + id);
		
		return res;
	}
}
