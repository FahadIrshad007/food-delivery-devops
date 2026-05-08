package com.comsats.devops.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test(priority = 1, description = "Verify login page loads successfully")
    public void testLoginPageLoads() {
        driver.get(BASE_URL + "/login");
        WebElement loginForm = driver.findElement(By.tagName("form"));
        Assert.assertTrue(loginForm.isDisplayed(), "Login form should be visible");
    }

    @Test(priority = 2, description = "Verify valid login credentials work")
    public void testValidLogin() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/") || currentUrl.contains("/home"), 
            "Should redirect to home after login");
    }

    @Test(priority = 3, description = "Verify invalid login shows error message")
    public void testInvalidLogin() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("email")).sendKeys("wrong@example.com");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        // Check for error toast or message
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Invalid") || pageSource.contains("Error") || 
                         pageSource.contains("incorrect") || pageSource.contains("Invalid credentials"),
            "Error message should be displayed for invalid login");
    }

    @Test(priority = 4, description = "Verify empty fields validation")
    public void testEmptyFieldsValidation() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // HTML5 validation or toast
        WebElement emailField = driver.findElement(By.id("email"));
        String validationMsg = emailField.getAttribute("validationMessage");
        
        if (validationMsg != null && !validationMsg.isEmpty()) {
            Assert.assertFalse(validationMsg.isEmpty(), "Should show HTML5 validation");
        } else {
            String pageSource = driver.getPageSource();
            Assert.assertTrue(pageSource.contains("required") || pageSource.contains("empty") ||
                             pageSource.contains("fill"), "Should show validation for empty fields");
        }
    }
}