package pages;

import config.LoadProp;
import driver.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryPage {
    private final WebDriver driver;

    private static final By CART_BUTTON  = By.cssSelector("[data-test='" + LoadProp.getUIConfig("cartButtonDataTest") + "']");
    private static final By SORT_DROPDOWN = By.cssSelector("[data-test='" + LoadProp.getUIConfig("sortDropdownDataTest") + "']");
    private static final By ITEM_PRICE   = By.cssSelector("[data-test='" + LoadProp.getUIConfig("individualItemPriceDivDataTest") + "']");

    public InventoryPage() {
        this.driver = BasePage.getDriver();
    }

    public void addItemsByDisplayNames(List<String> itemNames) {
        String xpathTemplate = LoadProp.getUIConfig("addToCartXPathTemplate");
        for (String itemName : itemNames) {
            driver.findElement(By.xpath(String.format(xpathTemplate, itemName))).click();
        }
    }

    public CartPage goToCart() {
        driver.findElement(CART_BUTTON).click();
        return new CartPage();
    }

    public void sortBy(String sortOption) {
        Select select = new Select(driver.findElement(SORT_DROPDOWN));
        select.selectByValue(sortOption);
    }

    public List<Double> getDisplayedPrices() {
        return driver.findElements(ITEM_PRICE)
                .stream()
                .map(WebElement::getText)
                .map(priceText -> Double.parseDouble(priceText.replaceAll("[^0-9.]", "")))
                .collect(Collectors.toList());
    }

    public boolean isPricesSortedLowToHigh() {
        List<Double> prices = getDisplayedPrices();
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                return false;
            }
        }
        return true;
    }
}
