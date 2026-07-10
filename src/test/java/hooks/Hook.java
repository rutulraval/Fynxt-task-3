package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import io.cucumber.java.Scenario;
import driver.BasePage;
import driver.BrowserSetup;
import config.LoadProp;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;

public class Hook extends BasePage {

    BrowserSetup browsersetup = new BrowserSetup();
    private static final int WAIT_SEC = 20;

    @Before("@ui")
    public void initializeTest() {
        browsersetup.selectBrowser();
        BasePage.getDriver().manage().deleteAllCookies();
        BasePage.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(WAIT_SEC));
        BasePage.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(WAIT_SEC));
        BasePage.getDriver().manage().timeouts().scriptTimeout(Duration.ofSeconds(WAIT_SEC));
    }

    /**
     * Executed after each UI-tagged scenario.
     *
     * On failure:
     *  1. Saves a .png screenshot to ScreenshotLocation (filesystem evidence).
     *  2. Embeds the same bytes into the Cucumber scenario report so it appears
     *     in the HTML report downloaded from CI artifacts.
     */
    @After("@ui")
    public void tearDown(Scenario scenario) {
        if (BasePage.getDriver() == null) {
            return;
        }

        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }

        // Attempt clean shutdown of the browser session
        try {
            BasePage.getDriver().quit();
        } catch (Exception ex) {
            System.err.println("Browser session already closed for scenario: "
                    + scenario.getName() + ": " + ex.getMessage());
        } finally {
            // Remove ThreadLocal reference to prevent memory leaks across tests
            BasePage.removeDriver();
        }
    }

    private void captureScreenshot(Scenario scenario) {
        // Selenium always outputs PNG — use .png extension to match the actual format
        String screenshotFilename = scenario.getName().replace(" ", "")
                + new Timestamp(new Date().getTime()).toString().replaceAll("[^a-zA-Z0-9]", "")
                + "_" + LoadProp.getProperty("Browser") + ".png";

        String screenshotDir = LoadProp.getProperty("ScreenshotLocation");

        try {
            // Guarantee the screenshots directory exists before writing
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Capture as bytes so we can both persist to disk AND embed in the report
            byte[] screenshotBytes = ((TakesScreenshot) BasePage.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

            // 1. Persist to disk for local inspection / CI artifact upload
            FileUtils.writeByteArrayToFile(new File(screenshotDir + screenshotFilename), screenshotBytes);
            System.out.println("📸 Screenshot saved: " + screenshotDir + screenshotFilename);

            // 2. Embed in the Cucumber HTML report (visible in test-output/ HTML artifact)
            scenario.attach(screenshotBytes, "image/png", screenshotFilename);

        } catch (IOException e) {
            // Log but don't mask the original test failure
            System.err.println("⚠ Could not capture screenshot for scenario '"
                    + scenario.getName() + "': " + e.getMessage());
        }
    }
}
