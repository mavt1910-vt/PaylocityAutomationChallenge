package UI.pages;

import UI.core.BaseUI;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BaseUI {

    @FindBy(id = "Username")
    private WebElement usernameTextBox;

    @FindBy(id = "Password")
    private WebElement passwordTextBox;

    @FindBy(css = "button.btn.btn-primary[type='submit']")
    private WebElement loginButton;

    public LoginPage open(String url) {
        navigateTo(url);
        return this;
    }

    public LoginPage typeUsername(String username) {
        type(usernameTextBox, username);
        return this;
    }

    public LoginPage typePassword(String password) {
        type(passwordTextBox, password);
        return this;
    }

    public BenefitsDashboardPage clickLogin() {
        click(loginButton);
        return new BenefitsDashboardPage();
    }
}
