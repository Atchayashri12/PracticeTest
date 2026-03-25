package com.example.grades;

import org.junit.After;

import org.junit.Before;

import org.junit.Test;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
 
import static org.junit.Assert.*;
 
public class NavbarTest {

    private WebDriver driver;
 
    @Before

    public void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

    }
 
    @Test

    public void testLoginButtonVisibleWhenNotLoggedIn() {

        

        driver.get("http://localhost:5173");

        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("localStorage.removeItem('basicToken');");

        driver.navigate().refresh();
 
        WebElement loginButton = driver.findElement(By.cssSelector("a.btn-outline-light"));

        assertEquals("Login", loginButton.getText());
 
      

        loginButton.click();

        assertTrue(driver.getCurrentUrl().endsWith("/login"));

    }
 
    @Test

    public void testLogoutButtonVisibleWhenLoggedIn() throws InterruptedException {

        driver.get("http://localhost:5173");

        

        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("localStorage.setItem('basicToken','dummyToken');");

        driver.navigate().refresh();
 
        WebElement logoutButton = driver.findElement(By.cssSelector("button.btn-outline-light"));

        assertEquals("Logout", logoutButton.getText());
 
       

        logoutButton.click();

        Thread.sleep(1000); 

        String currentUrl = driver.getCurrentUrl();

        assertTrue(currentUrl.endsWith("/login"));
 
      

        Object token = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return localStorage.getItem('basicToken');");

        assertNull(token);

    }
 
    @After

    public void tearDown() {

        driver.quit();

    }

}

 