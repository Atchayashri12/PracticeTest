package com.example.grades;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MarksFormUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    
    private static final String BASE = "http://localhost:5173";
    private static final String STUDENT_ID = "1";
    private static final String MARKS_URL = BASE + "/students/" + STUDENT_ID + "/marks";


    private static boolean isServerReachable(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            return code >= 200 && code < 500; // 2xx/3xx or even 4xx means server is up
        } catch (Exception e) {
            return false;
        }
    }

    

    @BeforeAll
    static void setup() {
        
        assumeTrue(isServerReachable(BASE + "/"), "Dev server not reachable at " + BASE + " — skipping UI tests.");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        
        options.addArguments("--disable-infobars");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().window().setSize(new Dimension(1366, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        
        driver.get(BASE + "/");
        ((JavascriptExecutor) driver)
                .executeScript("window.localStorage.setItem('basicToken', btoa('admin:admin123'));");
    }

    @AfterAll
    static void teardown() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
        }
    }

   

    private void waitForReady() {
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    private WebElement byLabelInput(String labelText) {
        
        String xpath = "//label[normalize-space()='" + labelText + "']/following::input[1]";
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    private void typeInto(WebElement input, String text) {
        wait.until(ExpectedConditions.elementToBeClickable(input)).clear();
        input.sendKeys(text);
    }

    private void clickSaveMarks() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Save Marks']")));
        btn.click();
    }

    private WebElement errorBannerIfAny() {
        try {
            return driver.findElement(By.cssSelector("div.text-danger"));
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    private void openMarksForm() {
        driver.navigate().to(MARKS_URL);
        waitForReady();

        WebElement h1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h4[normalize-space()='Add/Update Semester Marks']")));
        assertTrue(h1.isDisplayed(), "Form heading should be visible");

        WebElement h2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h4[normalize-space()='Existing Marks']")));
        assertTrue(h2.isDisplayed(), "Existing Marks heading should be visible");
    }


    @Test
    @Order(1)
    @DisplayName("Case 1: Renders form with inputs & buttons")
    void rendersForm() {
        openMarksForm();

        assertNotNull(byLabelInput("Semester (1-8)"));
        assertNotNull(byLabelInput("Assignment (0-100)"));
        assertNotNull(byLabelInput("Midterm (0-100)"));
        assertNotNull(byLabelInput("Final (0-100)"));

        WebElement saveBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[normalize-space()='Save Marks']")));
        assertTrue(saveBtn.isDisplayed());

        WebElement backBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[normalize-space()='Back']")));
        assertTrue(backBtn.isDisplayed());
    }

    @Test
    @Order(2)
    @DisplayName("Case 2: Semester out of range => 'Semester must be 1-8'")
    void semesterOutOfRange() {
        openMarksForm();

        typeInto(byLabelInput("Semester (1-8)"), "0");   // invalid
        typeInto(byLabelInput("Assignment (0-100)"), "10");
        typeInto(byLabelInput("Midterm (0-100)"), "20");
        typeInto(byLabelInput("Final (0-100)"), "30");

        clickSaveMarks();

        WebElement err = errorBannerIfAny();
        assertNotNull(err, "Error banner should be present");
        assertTrue(err.isDisplayed(), "Error banner should be visible");
        assertEquals("Semester must be 1-8", err.getText().trim());
    }

    @Test
    @Order(3)
    @DisplayName("Case 3: Any score out of range => 'Scores must be 0-100'")
    void scoresOutOfRange() {
        openMarksForm();

        typeInto(byLabelInput("Semester (1-8)"), "3");    // valid semester
        typeInto(byLabelInput("Assignment (0-100)"), "101"); // invalid score
        typeInto(byLabelInput("Midterm (0-100)"), "20");
        typeInto(byLabelInput("Final (0-100)"), "30");

        clickSaveMarks();

        WebElement err = errorBannerIfAny();
        assertNotNull(err, "Error banner should be present");
        assertTrue(err.isDisplayed(), "Error banner should be visible");
        assertEquals("Scores must be 0-100", err.getText().trim());
    }

    @Test
    @Order(4)
    @DisplayName("Case 4: Editing to valid values clears previous error after reload")
    void validValuesClearError() {
        openMarksForm();

        // Trigger an error first
        typeInto(byLabelInput("Semester (1-8)"), "0");
        typeInto(byLabelInput("Assignment (0-100)"), "10");
        typeInto(byLabelInput("Midterm (0-100)"), "20");
        typeInto(byLabelInput("Final (0-100)"), "30");
        clickSaveMarks();

        WebElement err = errorBannerIfAny();
        assertNotNull(err);
        assertTrue(err.isDisplayed());

        // Now set all valid values (we won't submit to avoid hitting API)
        byLabelInput("Semester (1-8)").clear(); byLabelInput("Semester (1-8)").sendKeys("5");
        byLabelInput("Assignment (0-100)").clear(); byLabelInput("Assignment (0-100)").sendKeys("25");
        byLabelInput("Midterm (0-100)").clear(); byLabelInput("Midterm (0-100)").sendKeys("35");
        byLabelInput("Final (0-100)").clear(); byLabelInput("Final (0-100)").sendKeys("85");

        // Reload to clear client-side error state (no API required)
        openMarksForm();

        WebElement maybeErr = errorBannerIfAny();
        if (maybeErr != null) {
            assertFalse(maybeErr.isDisplayed(), "Error banner should not be visible after reload");
        }
    }
}