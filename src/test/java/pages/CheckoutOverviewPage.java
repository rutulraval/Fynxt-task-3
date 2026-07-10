package pages;

import config.LoadProp;
import driver.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CheckoutOverviewPage {
    private final WebDriver driver;

    private static final By SUBTOTAL_LABEL = By.cssSelector("[data-test='" + LoadProp.getUIConfig("itemTotalDivData-Test") + "']");
    private static final By TAX_LABEL      = By.cssSelector("[data-test='" + LoadProp.getUIConfig("taxDivDataTest") + "']");
    private static final By ITEM_PRICE     = By.cssSelector("[data-test='" + LoadProp.getUIConfig("individualItemPriceDivDataTest") + "']");

    public CheckoutOverviewPage() {
        this.driver = BasePage.getDriver();
    }

    public String getItemTotalText() {
        return driver.findElement(SUBTOTAL_LABEL).getText();
    }

    public String getTaxText() {
        return driver.findElement(TAX_LABEL).getText();
    }

    /**
     * Returns numeric subtotal value parsed from the subtotal label (e.g. "Item total: $39.98" -> 39.98)
     */
    public double getSubtotalValue() {
        return parseCurrencyValue(getItemTotalText());
    }

    public double getTaxValue() {
        return parseCurrencyValue(getTaxText());
    }

    /**
     * Sums individual item prices shown on the overview page.
     */
    public double getSumOfItemPrices() {
        List<WebElement> prices = driver.findElements(ITEM_PRICE);
        double sum = 0.0;
        for (WebElement p : prices) {
            sum += parseCurrencyValue(p.getText());
        }
        return sum;
    }

    private double parseCurrencyValue(String text) {
        if (text == null) return 0.0;
        String num = text.replaceAll("[^0-9.]", "");
        if (num.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public boolean isItemTotalCorrect() {
        return Math.abs(getSumOfItemPrices() - getSubtotalValue()) < 0.01;
    }

    public boolean isTaxCorrect(int taxPercentage) {
        double expectedTax = getSubtotalValue() * taxPercentage / 100.0;
        return Math.abs(expectedTax - getTaxValue()) < 0.01;
    }
}
