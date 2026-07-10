package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "hooks"},
        tags = "@ui or (@api and not @mock)",
        plugin = {
                "pretty",
                "html:test-output/cucumber-reports/cucumber-pretty.html",
                "json:test-output/cucumber-reports/CucumberTestReport.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        })
public class RunnerTest extends AbstractTestNGCucumberTests {
}
