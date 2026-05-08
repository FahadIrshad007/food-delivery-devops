package com.comsats.devops.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected WebDriver driver;
    
    // Updated with your specific EC2 IP
    protected final String BASE_URL = "http://16.16.149.7:3000"; 
    protected final String API_URL = "http://16.16.149.7:5000";

    @BeforeMethod
    public void setUp() {
        // Comment this out because the Docker image already provides the binary
        // WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        
        options.addArguments("--headless=new"); 
        options.addArguments("--no-sandbox"); 
        options.addArguments("--disable-dev-shm-usage"); 
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}