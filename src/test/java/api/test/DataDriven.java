package api.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import api.endpoints.User_endpoints;
import api.payload.UserPojo;
import api.utilities.DataProviders;
import io.restassured.response.Response;

public class DataDriven {

	@Test(priority=1, dataProvider = "data", dataProviderClass = DataProviders.class)
	public void testPostUser(String name, String email, String gender, String status) {
		
		UserPojo up = new UserPojo();
		
		up.setName(name);
		up.setEmail(email);
		up.setGender(gender);
		up.setStatus(status);
		
		Response resp = User_endpoints.postUser(up);
		Assert.assertEquals(resp.statusCode(), 201);
	}
	
//	@Test(priority=2, dataProvider="getUserName", dataProviderClass=DataProviders.class)
//	public void testDeleteUser(String userNames) {
//		
//		System.out.println("username used = " + userNames);
//		
//		Response resp = User_endpoints.deleteUser(userNames);
//		Assert.assertEquals(resp.statusCode(), 200);
//	}
	
}
