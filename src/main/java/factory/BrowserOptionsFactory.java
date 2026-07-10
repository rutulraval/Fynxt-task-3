package factory;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

public final class BrowserOptionsFactory {

    private BrowserOptionsFactory() {
        // prevent instantiation
    }

    public static ChromeOptions getChromeOptions() {

        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-notifications");
        options.addArguments("--incognito");
        options.addArguments("--window-size=1920,1080");

        return options;
    }

    public static ChromeOptions getChromeHeadlessOptions() {

        ChromeOptions options = getChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");



        return options;
    }

    public static EdgeOptions getEdgeOptions() {

        EdgeOptions options = new EdgeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-notifications");
        options.addArguments("--inprivate");
        options.addArguments("--start-maximized");

        return options;
    }

    public static FirefoxOptions getFirefoxOptions() {

        FirefoxOptions options = new FirefoxOptions();

        // Disable password manager
        options.addPreference("signon.rememberSignons", false);
        options.addPreference("signon.autofillForms", false);

        // Disable notifications
        options.addPreference("dom.webnotifications.enabled", false);

        // Private mode
        options.addPreference("browser.privatebrowsing.autostart", true);

        return options;
    }
}