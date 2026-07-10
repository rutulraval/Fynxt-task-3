package pages;

import config.LoadProp;
import driver.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class CartPage {
    private final WebDriver driver;

    private static final By CART_BADGE       = By.cssSelector("[data-test='" + LoadProp.getUIConfig("cartItemCountSpanValueDataTest") + "']");
    private static final By CART_ITEM_ROWS   = By.cssSelector(LoadProp.getUIConfig("cartItemRowsCss"));
    private static final By CART_ITEM_QTY    = By.cssSelector(LoadProp.getUIConfig("cartItemQtySpanCss"));
    private static final By CHECKOUT_BUTTON  = By.id(LoadProp.getUIConfig("checkoutButtonId"));
    private static final By REMOVE_BUTTONS   = By.cssSelector(LoadProp.getUIConfig("removeButtonCss"));

    public CartPage() {
        this.driver = BasePage.getDriver();
    }

    public int getCartItemCount() {
        try {
            return Integer.parseInt(driver.findElement(CART_BADGE).getText());
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Integer> getCartItemQuantities() {
        return driver.findElements(CART_ITEM_ROWS)
                .stream()
                .map(row -> row.findElement(CART_ITEM_QTY).getText())
                .map(text -> {
                    try {
                        return Integer.parseInt(text.trim());
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }

    public void removeItemsByDisplayNames(List<String> itemNames) {
        String xpathTemplate = LoadProp.getUIConfig("removeFromCartXPathTemplate");
        for (String itemName : itemNames) {
            driver.findElement(By.xpath(String.format(xpathTemplate, itemName))).click();
        }
    }

    public void removeAllItems() {
        List<WebElement> removeButtons = driver.findElements(REMOVE_BUTTONS);
        // Iterate in reverse so stale references don't affect indices
        for (int i = removeButtons.size() - 1; i >= 0; i--) {
            removeButtons.get(i).click();
        }
    }

    public boolean isCartBadgeVisible() {
        try {
            return driver.findElement(CART_BADGE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public CheckoutPage clickCheckout() {
        driver.findElement(CHECKOUT_BUTTON).click();
        return new CheckoutPage();
    }
}
