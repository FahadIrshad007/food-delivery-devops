package com.comsats.devops.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class IntegrationTests extends BaseTest {

    @Test(description = "Verify API root endpoint returns 200")
    public void testApiHealth() {
        RestAssured.baseURI = API_URL;
        Response response = given()
            .when()
            .get("/")
            .then()
            .extract().response();
        
        Assert.assertEquals(response.statusCode(), 200, "API should be healthy");
        Assert.assertTrue(response.getBody().asString().contains("API Working"),
            "API should return 'API Working' message");
    }

    @Test(description = "Verify food API endpoint returns data")
    public void testFoodApiEndpoint() {
        RestAssured.baseURI = API_URL;
        Response response = given()
            .when()
            .get("/api/food/list")
            .then()
            .extract().response();
        
        Assert.assertEquals(response.statusCode(), 200, "Food API should return 200");
        Assert.assertTrue(response.getBody().asString().contains("name") || 
                         response.getBody().asString().contains("price"),
            "Food API should return food items with name and price");
    }

    @Test(description = "Verify user registration API works")
    public void testUserRegistrationApi() {
        RestAssured.baseURI = API_URL;
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        
        Response response = given()
            .contentType("application/json")
            .body("{\"name\":\"Test User\",\"email\":\"" + uniqueEmail + "\",\"password\":\"testpass123\"}")
            .when()
            .post("/api/user/register")
            .then()
            .extract().response();
        
        Assert.assertTrue(response.statusCode() == 200 || response.statusCode() == 201,
            "Registration should succeed with 200 or 201");
    }

    @Test(description = "Verify frontend-backend integration - food data flows correctly")
    public void testFrontendBackendIntegration() {
        // Get food from API
        RestAssured.baseURI = API_URL;
        Response apiResponse = given()
            .when()
            .get("/api/food/list")
            .then()
            .extract().response();
        
        Assert.assertEquals(apiResponse.statusCode(), 200);
        String apiFoodName = apiResponse.jsonPath().getString("[0].name");
        
        // Verify it appears on frontend
        driver.get(BASE_URL + "/menu");
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains(apiFoodName), 
            "Food from API should appear on frontend menu page");
    }

    @Test(description = "Verify authentication flow with JWT")
    public void testAuthTokenFlow() {
        // Register first
        RestAssured.baseURI = API_URL;
        String uniqueEmail = "authtest" + System.currentTimeMillis() + "@example.com";
        
        given()
            .contentType("application/json")
            .body("{\"name\":\"Auth Test\",\"email\":\"" + uniqueEmail + "\",\"password\":\"authpass123\"}")
            .when()
            .post("/api/user/register");
        
        // Login to get token
        Response loginResponse = given()
            .contentType("application/json")
            .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"authpass123\"}")
            .when()
            .post("/api/user/login")
            .then()
            .extract().response();
        
        Assert.assertEquals(loginResponse.statusCode(), 200);
        String token = loginResponse.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should be returned after login");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
        
        // Use token to access protected endpoint
        Response protectedResponse = given()
            .header("token", token)
            .when()
            .get("/api/cart/get")
            .then()
            .extract().response();
        
        Assert.assertEquals(protectedResponse.statusCode(), 200,
            "Protected endpoint should be accessible with valid token");
    }
}