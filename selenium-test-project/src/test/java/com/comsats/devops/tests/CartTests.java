package com.comsats.devops.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CartTests extends BaseTest {

    @Test(description = "Verify cart page is accessible")
    public void testCartPageAccessible() {
        driver.get(BASE_URL + "/cart");
        
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/cart"), "Should navigate to cart page");
    }

    @Test(description = "Verify cart shows empty state or items")
    public void testCartContent() {
        driver.get(BASE_URL + "/cart");
        
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        String pageSource = driver.getPageSource().toLowerCase();
        boolean hasEmptyMessage = pageSource.contains("empty") || pageSource.contains("no items");
        boolean hasItems = driver.findElements(By.cssSelector("[class*='cart-item']")).size() > 0;
        
        Assert.assertTrue(hasEmptyMessage || hasItems, "Cart should show empty message or items");
    }
}