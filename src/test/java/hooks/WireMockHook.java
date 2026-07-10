package hooks;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import config.LoadProp;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockHook {
    private static WireMockServer wireMockServer;
    // WireMock port and file root are externalised in TestData.properties
    // (keys: wiremockPort, wiremockFilesRoot)
    private static final int WIREMOCK_PORT = Integer.parseInt(LoadProp.getProperty("wiremockPort"));
    // WireMock expects the root folder that contains both mappings/ and __files/.
    private static final String WIREMOCK_FILES_ROOT_DIR = LoadProp.getProperty("wiremockFilesRoot");

    @Before("@mock")
    public void startWireMock() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            wireMockServer = new WireMockServer(wireMockConfig()
                    .port(WIREMOCK_PORT)
                    .usingFilesUnderDirectory(WIREMOCK_FILES_ROOT_DIR)
                    .notifier(new ConsoleNotifier(true)));
            wireMockServer.start();
            System.out.println("-------=====------ WireMock started on port " + WIREMOCK_PORT);
        }
    }

    @After("@mock")
    public void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            System.out.println("-------=====------ WireMock stopped");
        }
    }
}