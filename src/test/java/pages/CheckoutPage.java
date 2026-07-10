package pages;

import config.LoadProp;
import driver.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage {
    private final WebDriver driver;

    private static final By FIRST_NAME      = By.id(LoadProp.getUIConfig("firstNameId"));
    private static final By LAST_NAME       = By.id(LoadProp.getUIConfig("lastNameId"));
    private static final By POSTAL_CODE     = By.id(LoadProp.getUIConfig("postalCodeId"));
    private static final By CONTINUE_BUTTON = By.id(LoadProp.getUIConfig("continueButtonId"));
    private static final By ERROR_MESSAGE   = By.cssSelector("[data-test='" + LoadProp.getUIConfig("checkoutErrorDataTest") + "']");

    public CheckoutPage() {
        this.driver = BasePage.getDriver();
    }

    public CheckoutOverviewPage fillCheckoutAndContinue(String firstName, String lastName, String postalCode) {
        driver.findElement(FIRST_NAME).clear();
        driver.findElement(FIRST_NAME).sendKeys(firstName);
        driver.findElement(LAST_NAME).clear();
        driver.findElement(LAST_NAME).sendKeys(lastName);
        driver.findElement(POSTAL_CODE).clear();
        driver.findElement(POSTAL_CODE).sendKeys(postalCode);
        driver.findElement(CONTINUE_BUTTON).click();
        return new CheckoutOverviewPage();
    }

    public void continueWithoutFillingZip(String firstName, String lastName) {
        driver.findElement(FIRST_NAME).clear();
        driver.findElement(FIRST_NAME).sendKeys(firstName);
        driver.findElement(LAST_NAME).clear();
        driver.findElement(LAST_NAME).sendKeys(lastName);
        // Leave postal code blank intentionally
        driver.findElement(CONTINUE_BUTTON).click();
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
