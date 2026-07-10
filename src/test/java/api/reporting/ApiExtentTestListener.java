package api.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ApiExtentTestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ApiExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getTestClass().getRealClass().getSimpleName()
                + "."
                + result.getMethod().getMethodName()
                + formatParameters(result.getParameters());
        ApiExtentManager.createTest(testName, result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ApiExtentManager.getCurrentTest();
        if (test != null) {
            logTransaction(test);
            test.pass("Test passed");
        }
        cleanup();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ApiExtentManager.getCurrentTest();
        if (test != null) {
            logTransaction(test);
            if (result.getThrowable() != null) {
                test.fail(result.getThrowable());
            } else {
                test.fail("Test failed without throwable details.");
            }
        }
        cleanup();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ApiExtentManager.getCurrentTest();
        if (test != null) {
            if (result.getThrowable() != null) {
                test.skip(result.getThrowable());
            } else {
                test.skip("Test skipped");
            }
        }
        cleanup();
    }

    @Override
    public void onFinish(ITestContext context) {
        ApiExtentManager.flush();
    }

    private void logTransaction(ExtentTest test) {
        ApiReportContext.ApiTransaction transaction = ApiReportContext.getCurrentTransaction();
        if (transaction == null) {
            test.warning("No API transaction details were recorded for this test.");
            return;
        }

        test.log(Status.INFO, MarkupHelper.createCodeBlock(transaction.getRequestSummary()));
        test.log(Status.INFO, MarkupHelper.createCodeBlock(transaction.getResponseSummary()));
    }

    private void cleanup() {
        ApiReportContext.clear();
        ApiExtentManager.unloadTest();
    }

    private String formatParameters(Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return "";
        }
        return " [" + Arrays.stream(parameters)
                .map(String::valueOf)
                .collect(Collectors.joining(", ")) + "]";
    }
}


