package com.comsats.devops.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class FoodTests extends BaseTest {

    @Test(description = "Verify food menu page loads and displays items")
    public void testFoodMenuPage() {
        driver.get(BASE_URL + "/menu");
        
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        List<WebElement> foodItems = driver.findElements(By.cssSelector("[class*='food'], [class*='item'], [class*='card']"));
        Assert.assertFalse(foodItems.isEmpty(), "Food items should be displayed on menu page");
    }

    @Test(description = "Verify food item has image and price")
    public void testFoodItemDetails() {
        driver.get(BASE_URL + "/menu");
        
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        List<WebElement> foodItems = driver.findElements(By.cssSelector("[class*='food'], [class*='item'], [class*='card']"));
        if (!foodItems.isEmpty()) {
            WebElement firstItem = foodItems.get(0);
            // Check for image
            List<WebElement> images = firstItem.findElements(By.tagName("img"));
            Assert.assertFalse(images.isEmpty(), "Food item should have an image");
            
            // Check for price text
            String itemText = firstItem.getText();
            Assert.assertTrue(itemText.contains("$") || itemText.contains("Rs") || itemText.contains("PKR"),
                "Food item should display price");
        }
    }

    @Test(description = "Verify add to cart button exists on food items")
    public void testAddToCartButton() {
        driver.get(BASE_URL + "/menu");
        
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        boolean hasAddToCart = buttons.stream().anyMatch(b -> 
            b.getText().toLowerCase().contains("add") || 
            b.getText().toLowerCase().contains("cart"));
        
        Assert.assertTrue(hasAddToCart, "Add to cart button should exist");
    }
}