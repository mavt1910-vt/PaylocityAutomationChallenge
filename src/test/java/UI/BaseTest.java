package UI;

import UI.core.BrowserType;
import UI.core.ConfigManager;
import UI.core.DriverManager;
import UI.core.DriverOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class BaseTest {
    protected String url;
    protected String user;
    protected String password;

    @BeforeMethod
    @Parameters({"browser", "headless", "remote", "gridUrl"})
    public void setUp(@Optional("CHROME") String browser,
                      @Optional("false") String headless,
                      @Optional("false") String remote,
                      @Optional("") String gridUrl) {

        BrowserType bt = parseBrowser(browser);
        boolean isHeadless = Boolean.parseBoolean(headless);
        boolean isRemote   = Boolean.parseBoolean(remote);
        String grid        = (gridUrl == null || gridUrl.isBlank()) ? null : gridUrl;

        DriverManager.start(new DriverOptions(bt, isHeadless, isRemote, grid));
        url = ConfigManager.baseUrl();
        user = ConfigManager.user();
        password = ConfigManager.password();
    }

    @AfterMethod
    public void tearDown(){
        DriverManager.stop();
    }

    private BrowserType parseBrowser(String b) {
        if (b == null) return BrowserType.CHROME;
        try { return BrowserType.valueOf(b.trim().toUpperCase()); }
        catch (Exception e) { return BrowserType.CHROME; }
    }
}
