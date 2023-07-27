package api.endpoints;

import static io.restassured.RestAssured.given;

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
		
		String url = getURL().getString("postEndpoint");
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
}