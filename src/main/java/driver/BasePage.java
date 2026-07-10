package driver;

import org.openqa.selenium.WebDriver;

public class BasePage {
    // Use ThreadLocal to support parallel test execution
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(WebDriver webDriver) {
        driver.set(webDriver);
    }

    public static void removeDriver() {
        driver.remove(); // prevents memory leaks
    }

}
