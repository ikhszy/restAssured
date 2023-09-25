package api.endpoints;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import api.payload.CommentsPojo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Comment_endpoints {
	
	static ResourceBundle getURL() {
		ResourceBundle config = ResourceBundle.getBundle("config");
		return config;
	}
	
	public static Response postComment (CommentsPojo payload) {
		
		String url = getURL().getString("commentsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("POST Comments \n");
		
		
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
	
	public static Response getCommentById (int id) {

		String url = getURL().getString("commentsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("GET Comments By ID \n");
		
		Response res = 
		given()
			.auth().oauth2(userToken)
		.when()
			.get(url + "/" + id);
		
		return res;
	}
	
	public static Response putCommentById (CommentsPojo payload, int id) {
		
		String url = getURL().getString("commentsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("PUT Comments \n");
		

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
	
	public static Response deleteCommentById(int id) {
		String url = getURL().getString("commentsEndpoint");
		String userToken = getURL().getString("token");
		System.out.println("DELETE Comments \n");
		
		Response res = 
				given()
					.auth().oauth2(userToken)
				.when()
					.delete(url + "/" + id);
				
				return res;
	}
	
	public static List<CommentsPojo> searchCommentsWithInt (String param, int value) {
		String url = getURL().getString("commentsEndpoint");
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
			.as(CommentsPojo[].class));
	}
}
