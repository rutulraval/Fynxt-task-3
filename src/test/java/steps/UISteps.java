package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import pages.CartPage;
import pages.CheckoutPage;
import pages.HomePage;
import pages.InventoryPage;
import pages.LoginPage;
import pages.CheckoutOverviewPage;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UISteps {

    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private CheckoutOverviewPage checkoutOverviewPage;
    private LoginPage loginPage;
    private String firstName;
    private String lastName;
    private String postalCode;


    @Given("I am on the home page")
    public void iAmOnTheHomePage() {
        new HomePage().open();
    }


    @And("I login in with the following details")
    public void iLoginInWithTheFollowingDetails(DataTable dt) {
        List<Map<String, String>> rows = dt.asMaps(String.class, String.class);
        Map<String, String> first = rows.get(0);
        String user = first.get("userName");
        String pass = first.get("Password");
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login(user, pass);
    }

    @And("I add the following items to the basket")
    public void iAddTheFollowingItemsToTheBasket(DataTable dt) {
        List<List<String>> rows = dt.asLists();
        List<String> itemNames = new ArrayList<>();
        for (List<String> row : rows) {
            itemNames.add(row.get(0).trim());
        }
        inventoryPage.addItemsByDisplayNames(itemNames);
    }

    @And("I  should see {int} items added to the shopping cart")
    public void iShouldSeeItemsAddedToTheShoppingCart(int expected) {
        // navigate to cart and assert
        cartPage = inventoryPage.goToCart();
        int actual = cartPage.getCartItemCount();
        Assert.assertEquals(actual, expected, "Cart item count mismatch");
    }

    @And("I click on the shopping cart")
    public void iClickOnTheShoppingCart() {
        cartPage = inventoryPage.goToCart();
    }

    @And("I verify that the QTY count for each item should be {int}")
    public void iVerifyThatTheQTYCountForEachItemShouldBe(int expectedQty) {
        Assert.assertTrue(expectedQty > 0, "Expected quantity must be positive");
        List<Integer> quantities = cartPage.getCartItemQuantities();
        Assert.assertFalse(quantities.isEmpty(), "No cart item quantities were found");
        Assert.assertEquals(quantities.size(), cartPage.getCartItemCount(), "Cart row count mismatch");
        quantities.forEach(quantity -> assertQuantity(quantity, expectedQty));
    }

    private void assertQuantity(Integer actualQty, int expectedQty) {
        Assert.assertEquals(actualQty.intValue(), expectedQty, "Cart quantity mismatch");
    }

    @And("I remove the following item:")
    public void iRemoveTheFollowingItem(DataTable dt) {
        List<List<String>> rows = dt.asLists();
        List<String> itemNames = new ArrayList<>();
        for (List<String> row : rows) {
            itemNames.add(row.get(0).trim());
        }
        cartPage.removeItemsByDisplayNames(itemNames);
    }

    @And("I remove all items from the cart")
    public void iRemoveAllItemsFromTheCart() {
        cartPage.removeAllItems();
    }

    @And("I click on the CHECKOUT button")
    public void iClickOnTheCHECKOUTButton() {
        checkoutPage = cartPage.clickCheckout();
    }

    @And("I type {string} for First Name")
    public void iTypeForFirstName(String firstName) {
        this.firstName = firstName;
    }

    @And("I type {string} for Last Name")
    public void iTypeForLastName(String lastName) {
        this.lastName = lastName;
    }


    @Given("I click on the CONTINUE button")
    public void iClickOnTheCONTINUEButton() {
        checkoutOverviewPage = checkoutPage.fillCheckoutAndContinue(firstName, lastName, postalCode);
    }

    @And("^I type \\\"([^\\\"]*)\\\" for ZIP/Postal Code$")
    public void iTypeForZIPPostalCode(String zip) {
        postalCode = zip;
    }

    @Then("Item total will be equal to the total of items on the list")
    public void item_total_will_be_equal_to_the_total_of_items_on_the_list() {
        Assert.assertTrue(
                checkoutOverviewPage.isItemTotalCorrect(),
                "Item total does not match sum of item prices");
    }

    @Then("a Tax rate of {int} % is applied to the total")
    public void a_tax_rate_of_is_applied_to_the_total(Integer taxRate) {
        Assert.assertTrue(
                checkoutOverviewPage.isTaxCorrect(taxRate),
                "Incorrect tax amount displayed");
    }

    // New steps for additional scenarios

    @Then("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String expectedError) {
        loginPage = new LoginPage();
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "Error message was not displayed");
        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(
                actualError.contains(expectedError),
                "Expected error message containing: " + expectedError + ", but got: " + actualError);
    }

    @And("I sort inventory by {string}")
    public void iSortInventoryBy(String sortOption) {
        inventoryPage.sortBy(sortOption);
    }

    @Then("the inventory items should be sorted by price from low to high")
    public void theInventoryItemsShouldBeSortedByPriceFromLowToHigh() {
        Assert.assertTrue(
                inventoryPage.isPricesSortedLowToHigh(),
                "Items are not sorted by price from low to high");
    }

    @Then("the shopping cart badge should not be visible")
    public void theShoppingCartBadgeShouldNotBeVisible() {
        Assert.assertFalse(
                cartPage.isCartBadgeVisible(),
                "Shopping cart badge should not be visible when cart is empty");
    }

    @And("I continue checkout without filling the ZIP code")
    public void iContinueCheckoutWithoutFillingTheZIPCode() {
        checkoutPage.continueWithoutFillingZip(firstName, lastName);
    }

    @Then("I should see an inline error message {string}")
    public void iShouldSeeAnInlineErrorMessage(String expectedError) {
        Assert.assertTrue(
                checkoutPage.isErrorMessageDisplayed(),
                "Error message was not displayed in checkout");
        String actualError = checkoutPage.getErrorMessage();
        Assert.assertTrue(
                actualError.contains(expectedError),
                "Expected error message containing: " + expectedError + ", but got: " + actualError);
    }
}

