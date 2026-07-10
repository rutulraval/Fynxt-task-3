package api.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ApiExtentManager {

    private static final Path REPORT_DIRECTORY = Paths.get("target", "extent-reports", "api");
    private static final Path REPORT_FILE = REPORT_DIRECTORY.resolve("API-Extent.html");
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private static ExtentReports extentReports;

    private ApiExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            extentReports = createInstance();
        }
        return extentReports;
    }

    public static ExtentTest createTest(String name, String description) {
        ExtentTest test = getInstance().createTest(name, description);
        CURRENT_TEST.set(test);
        return test;
    }

    public static ExtentTest getCurrentTest() {
        return CURRENT_TEST.get();
    }

    public static void unloadTest() {
        CURRENT_TEST.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    public static String getReportPath() {
        return REPORT_FILE.toString();
    }

    private static ExtentReports createInstance() {
        try {
            Files.createDirectories(REPORT_DIRECTORY);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create Extent report directory: " + REPORT_DIRECTORY, e);
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_FILE.toString());
        sparkReporter.config().setDocumentTitle("MissionQA API Extent Report");
        sparkReporter.config().setReportName("Pure TestNG API Execution Report");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setEncoding("UTF-8");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("report.type", "Pure TestNG API");
        extent.setSystemInfo("framework", "TestNG + Rest Assured");
        extent.setSystemInfo("report.path", REPORT_FILE.toAbsolutePath().toString());
        return extent;
    }
}

