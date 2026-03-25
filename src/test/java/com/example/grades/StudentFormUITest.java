package com.example.grades;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentFormUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE = "http://localhost:5173";
    private static final String NEW_STUDENT_URL = BASE + "/students/new";

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1366, 900));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));

        
        driver.get(BASE + "/");
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem('basicToken', btoa('admin:admin123'));"
        );
    }

    @AfterAll
    static void teardown() {
        if (driver != null) driver.quit();
    }


    private void waitForReady() {
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    private WebElement byLabelInput(String labelText) {
        
        String xpath = "//label[normalize-space()='" + labelText + "']/following::input[1]";
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    private WebElement feedbackUnder(String labelText) {
       
        String xpath = "//label[normalize-space()='" + labelText + "']" +
                       "/ancestor::div[contains(@class,'col-')]" +
                       "//div[contains(@class,'invalid-feedback')]";
        return driver.findElement(By.xpath(xpath));
    }

    private boolean hasInvalidClass(WebElement input) {
        String cls = input.getAttribute("class");
        return cls != null && cls.contains("is-invalid");
    }

    private void clickSave() {
        By saveBtn = By.cssSelector("button.btn.btn-primary");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
        btn.click();
    }

    private void typeInto(WebElement input, String text) {
        wait.until(ExpectedConditions.elementToBeClickable(input)).clear();
        input.sendKeys(text);
    }

    private void goToForm() {
        driver.navigate().to(NEW_STUDENT_URL);
        waitForReady();
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h4[contains(normalize-space(),'Add Student') or contains(normalize-space(),'Edit Student')]")));
        assertTrue(heading.getText().contains("Add Student"),
            "Expected 'Add Student' heading, got: " + heading.getText());
    }

  

    @Test
    @Order(1)
    @DisplayName("[Case 1] Renders Add Student form")
    void rendersForm() {
        goToForm();

    
        List<String> labels = Arrays.asList(
                "First Name", "Last Name", "Email",
                "Roll Number", "Department", "Year of Study (1-8)"
        );
        for (String label : labels) {
            WebElement input = byLabelInput(label);
            assertNotNull(input, "Missing input for label: " + label);
            assertTrue(input.isDisplayed(), "Input not visible for label: " + label);
        }
    }

    @Test
    @Order(2)
    @DisplayName("[Case 2] Shows required/out-of-range errors when saving empty/invalid")
    void showsValidationErrors() {
        goToForm();

       
        clickSave();

        
        WebElement fn = byLabelInput("First Name");
        WebElement ln = byLabelInput("Last Name");
        WebElement em = byLabelInput("Email");
        WebElement rn = byLabelInput("Roll Number");
        WebElement dp = byLabelInput("Department");
        WebElement yr = byLabelInput("Year of Study (1-8)");

       
        assertTrue(hasInvalidClass(fn), "First Name should be invalid");
        assertTrue(hasInvalidClass(ln), "Last Name should be invalid");

        
        assertTrue(hasInvalidClass(em), "Email should be invalid");

        assertTrue(hasInvalidClass(rn), "Roll Number should be invalid");
        assertTrue(hasInvalidClass(dp), "Department should be invalid");

       
        typeInto(yr, "0");
        clickSave();

        
        assertTrue(hasInvalidClass(yr), "Year should be invalid when out of range");

        
        assertTrue(feedbackUnder("First Name").isDisplayed(), "First Name feedback should be visible");
        assertTrue(feedbackUnder("Year of Study (1-8)").isDisplayed(), "Year feedback should be visible");
    }

    @Test
    @Order(3)
    @DisplayName("[Case 3] Invalid email format shows invalid state")
    void invalidEmailFormat() {
        goToForm();

        WebElement em = byLabelInput("Email");
        typeInto(em, "abc"); // clearly invalid
        clickSave();

        assertTrue(hasInvalidClass(em), "Email should be invalid for bad format");
        
        assertTrue(feedbackUnder("Email").isDisplayed(), "Email feedback should be visible");
    }

    @Test
    @Order(4)
    @DisplayName("[Case 4] Filling valid values clears all invalid states (client-side)")
    void fillingValidValuesClearsErrors() {
        goToForm();

        WebElement fn  = byLabelInput("First Name");
        WebElement ln  = byLabelInput("Last Name");
        WebElement em  = byLabelInput("Email");
        WebElement rn  = byLabelInput("Roll Number");
        WebElement dp  = byLabelInput("Department");
        WebElement yr  = byLabelInput("Year of Study (1-8)");

        
        clickSave();

        
        typeInto(fn, "Atchaya");
        typeInto(ln, "Shri");
        typeInto(em, "atchaya@example.com");
        typeInto(rn, "2024CS001");
        typeInto(dp, "CSE");
        yr.clear(); yr.sendKeys("4");

      
        clickSave();

        
        assertFalse(hasInvalidClass(fn), "First Name should be valid now");
        assertFalse(hasInvalidClass(ln), "Last Name should be valid now");
        assertFalse(hasInvalidClass(em), "Email should be valid now");
        assertFalse(hasInvalidClass(rn), "Roll Number should be valid now");
        assertFalse(hasInvalidClass(dp), "Department should be valid now");
        assertFalse(hasInvalidClass(yr), "Year should be valid now");

    }
}