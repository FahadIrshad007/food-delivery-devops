package com.comsats.devops.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class NavigationTests extends BaseTest {

    @Test(description = "Verify homepage loads and displays navigation")
    public void testHomepageNavigation() {
        driver.get(BASE_URL);
        List<WebElement> navLinks = driver.findElements(By.tagName("nav"));
        // If no nav tag, try header or div with class containing 'nav'
        if (navLinks.isEmpty()) {
            navLinks = driver.findElements(By.cssSelector("header, [class*='nav'], [class*='navbar']"));
        }
        Assert.assertFalse(navLinks.isEmpty(), "Navigation should exist");
    }

    @Test(description = "Verify all navigation links are clickable")
    public void testNavigationLinks() {
        driver.get(BASE_URL);
        List<WebElement> links = driver.findElements(By.cssSelector("nav a, header a, [class*='nav'] a"));
        
        for (WebElement link : links) {
            if (link.isDisplayed()) {
                Assert.assertTrue(link.isEnabled(), 
                    "Link " + link.getText() + " should be clickable");
            }
        }
    }

    @Test(description = "Verify page title consistency across routes")
    public void testPageTitles() {
        String[] routes = {"/", "/login", "/menu", "/cart"};
        for (String route : routes) {
            driver.get(BASE_URL + route);
            String title = driver.getTitle();
            Assert.assertNotNull(title);
            Assert.assertFalse(title.isEmpty(), "Page " + route + " should have a title");
        }
    }

    @Test(description = "Verify footer exists on all pages")
    public void testFooterPresence() {
        String[] pages = {"/", "/login", "/menu"};
        for (String page : pages) {
            driver.get(BASE_URL + page);
            List<WebElement> footers = driver.findElements(By.tagName("footer"));
            if (footers.isEmpty()) {
                footers = driver.findElements(By.cssSelector("[class*='footer']"));
            }
            Assert.assertFalse(footers.isEmpty(), "Footer should exist on " + page);
        }
    }
}