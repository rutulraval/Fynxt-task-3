package pages;

import config.LoadProp;
import driver.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private final WebDriver driver;

    private static final By USERNAME       = By.id(LoadProp.getUIConfig("usernameId"));
    private static final By PASSWORD       = By.id(LoadProp.getUIConfig("passwordId"));
    private static final By LOGIN_BUTTON   = By.id(LoadProp.getUIConfig("loginButtonId"));
    private static final By ERROR_MESSAGE  = By.cssSelector("[data-test='" + LoadProp.getUIConfig("loginErrorDataTest") + "']");

    public LoginPage() {
        this.driver = BasePage.getDriver();
    }

    public InventoryPage login(String user, String pass) {
        driver.findElement(USERNAME).clear();
        driver.findElement(USERNAME).sendKeys(user);
        driver.findElement(PASSWORD).clear();
        driver.findElement(PASSWORD).sendKeys(pass);
        driver.findElement(LOGIN_BUTTON).click();
        return new InventoryPage();
    }

    public String getErrorMessage() {
        try {
            return driver.findElement(ERROR_MESSAGE).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return driver.findElement(ERROR_MESSAGE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
