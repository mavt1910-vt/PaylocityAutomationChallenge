package UI.pages;

import UI.core.BaseUI;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginUI extends BaseUI {

    @FindBy(id = "Username")
    private WebElement usernameTextBox;

    @FindBy(id = "Password")
    private WebElement passwordTextBox;

    @FindBy(css = "button.btn.btn-primary[type='submit']")
    private WebElement loginButton;

    public LoginUI open(String url) {
        navigateTo(url);
        return this;
    }

    public LoginUI typeUsername(String username) {
        type(usernameTextBox, username);
        return this;
    }

    public LoginUI typePassword(String password) {
        type(passwordTextBox, password);
        return this;
    }

    public BenefitsDashboardPage clickLogin() {
        click(loginButton);
        return new BenefitsDashboardPage();
    }
}
