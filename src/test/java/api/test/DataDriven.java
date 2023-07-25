//package api.test;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//import api.endpoints.User_endpoints;
//import api.payload.UserPojo;
//import api.utilities.DataProviders;
//import io.restassured.response.Response;
//
//public class DataDriven {
//
//	@Test(priority=1, dataProvider = "data", dataProviderClass = DataProviders.class)
//	public void testPostUser(String userID, String userName, String firstName, String lastName, String email, String password, String phone) {
//		
//		UserPojo up = new UserPojo();
//		
//		up.setId(Integer.parseInt(userID));
//		up.setUsername(userName);
//		up.setFirstname(firstName);
//		up.setLastname(lastName);
//		up.setEmail(email);
//		up.setPassword(password);
//		up.setPhone(phone);
//		
//		Response resp = User_endpoints.postUser(up);
//		Assert.assertEquals(resp.statusCode(), 200);
//	}
//	
//	@Test(priority=2, dataProvider="getUserName", dataProviderClass=DataProviders.class)
//	public void testDeleteUser(String userNames) {
//		
//		System.out.println("username used = " + userNames);
//		
//		Response resp = User_endpoints.deleteUser(userNames);
//		Assert.assertEquals(resp.statusCode(), 200);
//	}
//	
//}
