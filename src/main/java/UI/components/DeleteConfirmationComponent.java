package UI.components;

import UI.core.BaseUI;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DeleteConfirmationComponent extends BaseUI {

    @FindBy(id = "deleteId")
    private WebElement hiddenId;

    @FindBy(id = "deleteFirstName")
    private WebElement firstNameLabel;

    @FindBy(id = "deleteLastName")
    private WebElement lastNameLabel;

    @FindBy(id = "deleteEmployee")
    private WebElement deleteButton;

    @FindBy(id = "cancelEmployee")
    private WebElement cancelButton;

    public DeleteConfirmationComponent() {
        super();
        PageFactory.initElements(driver, this);
    }

    public String getDeleteId() {
        return getAttribute(hiddenId,"value");
    }

    public String getFirstName() {
        return getText(firstNameLabel);
    }

    public String getLastName() {
        return getText(lastNameLabel);
    }

    public void confirmDelete() {
        click(deleteButton);
    }

    public void cancelDelete() {
        click(cancelButton);
    }
}
