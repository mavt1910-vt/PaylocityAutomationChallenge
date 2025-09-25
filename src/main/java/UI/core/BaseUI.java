package UI.core;

import Common.LogHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseUI {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BaseUI() {
        this.driver = DriverManager.get();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
        LogHelper.info(this.getClass().getSimpleName() + " initialized");
    }

    protected WebElement waitUntilVisible(WebElement element) {
        LogHelper.debug("Waiting until element is visible: " + element);
        WebElement el = wait.until(ExpectedConditions.visibilityOf(element));
        LogHelper.debug("Element is visible: " + el);
        return el;
    }

    protected WebElement waitUntilClickable(WebElement element) {
        LogHelper.debug("Waiting until element is clickable: " + element);
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(element));
        LogHelper.debug("Element is clickable: " + el);
        return el;
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            boolean displayed = element.isDisplayed();
            LogHelper.debug("Element displayed=" + displayed + " -> " + element);
            return displayed;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            LogHelper.warn("Element not displayed or stale: " + element);
            return false;
        }
    }

    protected boolean isEnabled(WebElement element) {
        try {
            String disabledAttr = element.getAttribute("disabled");
            boolean enabled = element.isDisplayed() && element.isEnabled()
                    && !"true".equalsIgnoreCase(String.valueOf(disabledAttr));
            LogHelper.debug("Element enabled=" + enabled + " (disabledAttr=" + disabledAttr + ") -> " + element);
            return enabled;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            LogHelper.warn("Element not enabled or stale: " + element);
            return false;
        }
    }

    protected boolean isDisabled(WebElement element) {
        try {
            String disabledAttr = element.getAttribute("disabled");
            boolean disabled = !element.isEnabled() || "true".equalsIgnoreCase(String.valueOf(disabledAttr));
            LogHelper.debug("Element disabled=" + disabled + " (disabledAttr=" + disabledAttr + ") -> " + element);
            return disabled;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            LogHelper.warn("Element considered disabled due to exception: " + element);
            return true;
        }
    }

    protected boolean waitUntilEnabled(WebElement element) {
        LogHelper.debug("Waiting until element is enabled: " + element);
        try {
            wait.until(d -> isEnabled(element));
            LogHelper.debug("Element became enabled: " + element);
            return true;
        } catch (TimeoutException e) {
            LogHelper.warn("Timeout waiting for element to be enabled: " + element);
            return false;
        }
    }

    protected boolean waitUntilDisabled(WebElement element) {
        LogHelper.debug("Waiting until element is disabled: " + element);
        try {
            wait.until(d -> isDisabled(element));
            LogHelper.debug("Element became disabled: " + element);
            return true;
        } catch (TimeoutException e) {
            LogHelper.warn("Timeout waiting for element to be disabled: " + element);
            return false;
        }
    }

    protected void navigateTo(String url) {
        LogHelper.info("Navigating to: " + url);
        driver.get(url);
        LogHelper.debug("Navigation complete: " + url);
    }

    protected void click(WebElement element) {
        LogHelper.info("Clicking element: " + element);
        WebElement el = waitUntilClickable(element);
        scrollIntoView(el);
        el.click();
        LogHelper.debug("Clicked successfully: " + el);
    }

    protected void type(WebElement element, CharSequence text) {
        LogHelper.info("Typing into element: " + element + " -> " + text);
        WebElement el = waitUntilVisible(element);
        scrollIntoView(el);
        try {
            el.clear();
        } catch (InvalidElementStateException ignored) {
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        }
        el.sendKeys(text);
        LogHelper.debug("Text typed successfully into: " + el);
    }

    protected String getText(WebElement element) {
        try {
            String text = waitUntilVisible(element).getText();
            LogHelper.info("Got text: " + text);
            return text;
        } catch (TimeoutException | StaleElementReferenceException e) {
            LogHelper.warn("Could not get text, element not visible or stale: " + element);
            return null;
        }
    }

    protected String getAttribute(WebElement element, String attribute) {
        try {
            String value = element.getAttribute(attribute);
            LogHelper.info("Got attribute '" + attribute + "' = " + value);
            return value;
        } catch (Exception e) {
            LogHelper.error("Could not get attribute '" + attribute + "' from element: " + element);
            return null;
        }
    }

    protected void scrollIntoView(WebElement element) {
        try {
            LogHelper.debug("Scrolling into view: " + element);
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
            LogHelper.debug("Scrolled into view: " + element);
        } catch (JavascriptException e) {
            LogHelper.error("Failed to scroll into view: " + e.getMessage());
        }
    }

    public void setValueViaJs(WebElement el, String value) {
        LogHelper.info("Setting value via JS: " + el + " -> " + value);
        ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].value = arguments[1];" +
                                "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                                "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                        el, value
                );
        LogHelper.debug("Value set via JS: " + el);
    }
}
