package pages;

import config.LoadProp;
import driver.BasePage;

public class HomePage {

    public void open() {
        BasePage.getDriver().get(LoadProp.getProperty("url"));
    }
}

