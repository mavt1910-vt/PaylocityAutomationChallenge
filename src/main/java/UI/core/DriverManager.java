package UI.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.URL;
import java.time.Duration;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private DriverManager() {}

    public static void start(DriverOptions opts) {
        if (TL_DRIVER.get() != null) return;

        WebDriver driver = switch (opts.browser()) {
            case CHROME -> createChrome(opts);
            case FIREFOX -> createFirefox(opts);
            case EDGE -> createEdge(opts);
        };

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        if (!effectiveHeadless(opts)) {
            try { driver.manage().window().maximize(); } catch (Exception ignored) {}
        } else {
            try { driver.manage().window().setSize(new Dimension(1920, 1080)); } catch (Exception ignored) {}
        }

        TL_DRIVER.set(driver);
    }

    public static WebDriver get() {
        if (TL_DRIVER.get() == null) throw new IllegalStateException("Driver not started");
        return TL_DRIVER.get();
    }

    public static void stop() {
        WebDriver d = TL_DRIVER.get();
        if (d != null) {
            d.quit();
            TL_DRIVER.remove();
        }
    }

    private static WebDriver createChrome(DriverOptions opts) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        if (effectiveHeadless(opts)) {
            options.addArguments("--headless=new");
        }

        if (isDocker()) {
            options.addArguments(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--window-size=1920,1080"
            );
        }

        return opts.remote()
                ? new RemoteWebDriver(createUrl(opts.gridUrl()), options)
                : new ChromeDriver(options);
    }

    private static WebDriver createFirefox(DriverOptions opts) {
        FirefoxOptions options = new FirefoxOptions();

        if (effectiveHeadless(opts)) {
            options.addArguments("-headless");
        }

        if (isDocker()) {
            options.addArguments("--width=1920", "--height=1080");
        }

        return opts.remote()
                ? new RemoteWebDriver(createUrl(opts.gridUrl()), options)
                : new FirefoxDriver(options);
    }

    private static WebDriver createEdge(DriverOptions opts) {
        EdgeOptions options = new EdgeOptions();
        if (effectiveHeadless(opts)) {
            options.addArguments("--headless=new");
        }
        if (isDocker()) {
            options.addArguments(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--window-size=1920,1080"
            );
        }
        return opts.remote()
                ? new RemoteWebDriver(createUrl(opts.gridUrl()), options)
                : new EdgeDriver(options);
    }

    private static URL createUrl(String u) {
        try { return new URL(u); } catch (Exception e) { throw new RuntimeException(e); }
    }

    private static boolean isDocker() {
        try {
            if ("true".equalsIgnoreCase(System.getenv("RUNNING_IN_DOCKER"))) return true;
            return new File("/.dockerenv").exists();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean effectiveHeadless(DriverOptions opts) {
        return opts.headless() || isDocker();
    }
}
