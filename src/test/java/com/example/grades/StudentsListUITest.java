package com.example.grades;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentsListUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

   
    private static final String BASE = "http://localhost:5173";
    private static final String STUDENTS = BASE + "/students";

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

    private void waitForPageReady() {
        wait.until(d -> "complete".equals(((JavascriptExecutor) d)
                .executeScript("return document.readyState")));
    }

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    private WebElement waitPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    private WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    private void safeClick(By locator) {
        WebElement el = waitClickable(locator);
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", el);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", el);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Students page loads, shows heading and + Add Student")
    void loadsPageAndHeader() {
        driver.navigate().to(STUDENTS);
        waitForPageReady();

        WebElement heading = waitVisible(By.xpath("//h1|//h2|//h3|//h4|//h5|//h6"));
        assertTrue(heading.getText().toLowerCase().contains("students"),
                "Expected heading containing 'Students' but got: " + heading.getText());

        WebElement addBtn = waitPresent(By.xpath(
                "//a[normalize-space()='+ Add Student' or contains(normalize-space(), 'Add Student')]"));
        assertTrue(addBtn.isDisplayed(), "Expected '+ Add Student' to be visible");

        String href = addBtn.getAttribute("href");
        assertNotNull(href, "Add button href must not be null");
        assertTrue(href.contains("/students/new"), "Add button href should contain /students/new");
    }

    @Test
    @Order(2)
    @DisplayName("Table shows expected headers")
    void showsTableHeaders() {
        
        WebElement table = waitPresent(By.xpath("//div[contains(@class,'table-responsive')]//table"));
        assertNotNull(table, "Expected a table inside .table-responsive");

        String[] expected = {"#", "Name", "Roll", "Email", "Dept", "Year", "Actions"};

        List<WebElement> ths = table.findElements(By.cssSelector("thead th"));
        assertEquals(expected.length, ths.size(), "Unexpected header count");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], ths.get(i).getText().trim(), "Header mismatch at index " + i);
        }
    }

    @Test
    @Order(3)
    @DisplayName("Either shows empty state or at least one data row")
    void showsEitherEmptyStateOrRows() {
        boolean ok = new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(d -> {
                    
                    List<WebElement> emptyCells = d.findElements(By.xpath(
                            "//table//tbody//td[@colspan='7' and contains(., 'No students found.')]"));
                    if (!emptyCells.isEmpty()) return true;

                    
                    List<WebElement> rows = d.findElements(By.cssSelector("table tbody tr"));
                    if (!rows.isEmpty()) {
                        List<WebElement> tds = rows.get(0).findElements(By.tagName("td"));
                        return tds.size() >= 6 && !(tds.size() == 1 && "7".equals(tds.get(0).getAttribute("colspan")));
                    }
                    return false;
                });

        assertTrue(ok, "Expected 'No students found.' OR at least one data row");
    }

    @Test
    @Order(4)
    @DisplayName("Clicking + Add Student navigates to /students/new")
    void navigateToAddStudent() {
        By addStudent = By.xpath("//a[normalize-space()='+ Add Student' or contains(normalize-space(), 'Add Student')]");
        safeClick(addStudent);

        wait.until(ExpectedConditions.urlContains("/students/new"));

        
        try {
            WebElement firstName = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("input[name='firstName'], #firstName")));
            assertTrue(firstName.isDisplayed(), "Expected a First Name field on create form");
        } catch (TimeoutException ignored) {
            
        }
    }
}