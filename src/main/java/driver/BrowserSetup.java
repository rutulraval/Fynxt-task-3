package driver;

import config.LoadProp;
import factory.BrowserOptionsFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.text.MessageFormat;


public class BrowserSetup extends BasePage {

    public static String browser = null;


    /**
     * Browser property location TestData/TestData.properties (test resources)
     */


    /**
     * Function for multi browser
     */
    public void selectBrowser() {
        browser = LoadProp.getProperty("Browser");
        System.out.println("Browser: " + browser);

        if (browser.equalsIgnoreCase("Chrome")) {
            WebDriverManager.chromedriver().setup();
            if (System.getenv("GITHUB_ACTIONS") != null) {
                BasePage.setDriver(
                        new ChromeDriver(
                                BrowserOptionsFactory.getChromeHeadlessOptions()));
            } else {
                BasePage.setDriver(
                        new ChromeDriver(
                                BrowserOptionsFactory.getChromeOptions()));
            }
        } else if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            BasePage.setDriver(
                    new EdgeDriver(
                            BrowserOptionsFactory.getEdgeOptions()));
        } else if (browser.equalsIgnoreCase("Firefox")) {
            WebDriverManager.firefoxdriver().setup();
            BasePage.setDriver(
                    new FirefoxDriver(
                            BrowserOptionsFactory.getFirefoxOptions()));
        } else if (browser.equalsIgnoreCase("chromeMac")) {
            WebDriverManager.chromedriver().setup();
            BasePage.setDriver(
                    new ChromeDriver(
                            BrowserOptionsFactory.getChromeOptions()));
        } else if (browser.equalsIgnoreCase("chromeHeadless")) {
            WebDriverManager.chromedriver().setup();
            BasePage.setDriver(
                    new ChromeDriver(
                            BrowserOptionsFactory.getChromeHeadlessOptions()));
        }  else {
            throw new IllegalArgumentException(MessageFormat.format("Wrong Browser: {0}", browser));
        }
    }
}
