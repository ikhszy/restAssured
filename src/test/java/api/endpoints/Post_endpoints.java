package api.endpoints;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import api.payload.PostPojo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Post_endpoints {
	
	static ResourceBundle getURL() {
		ResourceBundle config = ResourceBundle.getBundle("config");
		return config;
	}
	
	public static Response postPost (PostPojo payload) {
		
		String url = getURL().getString("postsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Post using this as Request Body....\n");
		System.out.println(payload);
		
		
		Response res = 
		given()
			.auth().oauth2(userToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload)
		.when()
			.post(url);
		
		return res;
	}
	
	public static Response postGetById (int id) {
		
		String url = getURL().getString("postsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("GET by ID using this id: " + id + "\n");
		
		Response res = 
		given()
			.auth().oauth2(userToken)
		.when()
			.get(url + "/" + id);
		
		return res;
	}
	
	public static Response postPutById (PostPojo payload, int id) {
		String url = getURL().getString("postsEndpoint");
		String userToken = getURL().getString("token");
		
		Response res = 
		given()
			.auth().oauth2(userToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload)
		.when()
			.put(url + "/" + id);
		
		return res;
	}
	
	public static Response postDeleteById(int id) {
		String url = getURL().getString("postsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Deleting this id: " + id + "\n");
		
		Response res = 
		given()
			.auth().oauth2(userToken)
		.when()
			.delete(url + "/" + id);
		
		return res;
	}
	
	public static List<PostPojo> getPostOneParam (String param, int value) {
		String url = getURL().getString("postsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Filter post by " + param +" ....\n");
		
		return Arrays.asList(given()
			.auth().oauth2(userToken)
			.and()
			.queryParam(param, value)
		.when()
			.get(url)
		.then()
			.statusCode(200)
			.extract()
			.response()
			.body()
			.as(PostPojo[].class));
	}
	
	public static List<PostPojo> getAllPost() {
		String url = getURL().getString("postsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("Get all post ....\n");
		
		return Arrays.asList(given()
				.auth().oauth2(userToken)
				.when()
				.get(url)
				.then()
				.extract()
				.response()
				.body()
				.as(PostPojo[].class));
	}
	
public static Response getPostIdForOthers() {
		
		String url = getURL().getString("postsEndpoint");
		String userToken = getURL().getString("token");
		
		Response res = 
		given()
			.auth().oauth2(userToken)
		.when()
			.get(url);
		
		return res;
	}
}
