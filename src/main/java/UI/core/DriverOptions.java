package UI.core;

public record DriverOptions(BrowserType browser,
                            boolean headless,
                            boolean remote,
                            String gridUrl) {
}