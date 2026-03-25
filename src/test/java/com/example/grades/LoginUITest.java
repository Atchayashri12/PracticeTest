package com.example.grades;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginUITest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:5173";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String STUDENTS_PATH_FRAGMENT = "/students";

    @BeforeAll
    static void setupDriverBinary() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        
        options.addArguments("--window-size=1280,900");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(LOGIN_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[normalize-space()='Admin Login']")));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement inputByLabel(String labelText) {
        
        WebElement label = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[normalize-space()='" + labelText + "']")));
        
        WebElement container = label.findElement(By.xpath("./ancestor::*[contains(@class,'mb-3')][1]"));
        return container.findElement(By.cssSelector("input.form-control"));
    }

   
    private WebElement loginButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.btn.btn-primary")));
    }

    private void waitForUrlContains(String fragment) {
        assertTrue(wait.until(ExpectedConditions.urlContains(fragment)),
                "Expected URL to contain '" + fragment + "', but was: " + driver.getCurrentUrl());
    }

    private WebElement errorAlert() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert.alert-danger")));
    }

    @Test
    @Order(1)
    void testValidLogin() {
        WebElement username = inputByLabel("Username");
        WebElement password = inputByLabel("Password");
        WebElement button = loginButton();

     
        username.clear();
        username.sendKeys("admin");

        password.clear();
        password.sendKeys("admin123");

        button.click();

        
        waitForUrlContains(STUDENTS_PATH_FRAGMENT);

      
        try {
            WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(normalize-space(.), 'Students')]")));
            assertTrue(heading.isDisplayed(), "Expected 'Students' heading to be visible.");
        } catch (TimeoutException ignored) {
           
        }
    }

  }
