package UI.components;

import UI.core.BaseUI;
import UI.pages.BenefitsDashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class EmployeeFormComponent extends BaseUI {


    @FindBy(id = "firstName")
    private WebElement firstNameTextBox;

    @FindBy(id = "lastName")
    private WebElement lastNameTextBox;

    @FindBy(id = "dependants")
    private WebElement dependentsTextBox;

    @FindBy(id = "addEmployee")
    private WebElement addButton;

    @FindBy(id = "updateEmployee")
    private WebElement updateButton;

    public EmployeeFormComponent() {
        super();
        PageFactory.initElements(driver, this);
    }

    public BenefitsDashboardPage addEmployee(String first, String last, String dependents) {
        typeFirstName(first);
        typeLastName(last);
        typeDependents(dependents);
        clickAdd();
        return new BenefitsDashboardPage();
    }

    public EmployeeFormComponent typeFirstName(String firstName) {
        type(firstNameTextBox, firstName);
        return this;
    }

    public EmployeeFormComponent typeLastName(String lastName) {
        type(lastNameTextBox, lastName);
        return this;
    }

    public EmployeeFormComponent typeFirstNameRaw(String firstWithControlChars) {
        setValueViaJs(firstNameTextBox, firstWithControlChars);
        return this;
    }

    public EmployeeFormComponent typeLastNameRaw(String lastWithControlChars) {
        setValueViaJs(lastNameTextBox, lastWithControlChars);
        return this;
    }

    public EmployeeFormComponent typeDependents(String dependents) {
        type(dependentsTextBox, dependents);
        return this;
    }

    public void clickAdd() {
        click(addButton);
    }

    public boolean isFirstNameErrorVisible() {
        String xp = ".//*[contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), " +
                "'first name is required')]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDuplicateEmployeeErrorVisible() {
        String xp = ".//*[contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'duplicate')" +
                " or contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'already exists')" +
                " or contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'already registered')]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(ExpectedConditions
                    .presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNameTooLongErrorVisible() {
        String xp = ".//*[contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), " +
                "'must be 50 characters or fewer')]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(ExpectedConditions
                    .presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInvalidCharsErrorVisible() {
        String xp = ".//*[contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'invalid characters')]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(ExpectedConditions
                    .presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAddEnabled() {
        return isEnabled(addButton);
    }

    public boolean isDependentsNumericErrorVisible() {
        String xp = ".//*[contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'dependents') " +
                "and (contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'must be integer')" +
                " or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'must be numeric')" +
                " or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'invalid'))]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(ExpectedConditions
                    .presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUpdateConfirmationVisible() {
        String xp = ".//*[self::div or self::span or self::p or self::small or self::label]" +
                "[contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'updated')" +
                " or contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'success')" +
                " or contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'changes saved')" +
                " or contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'saved successfully')]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDependentsRangeErrorVisible() {
        String xp = ".//*[self::div or self::span or self::p or self::small or self::label]"
                + "[contains(translate(normalize-space(.),"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),"
                + " 'dependents')"
                + " and (contains(translate(normalize-space(.),"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'between 0 and 32')"
                + " or contains(translate(normalize-space(.),"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '0 to 32'))]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDependentsRequiredErrorVisible() {
        String xp = ".//*[self::div or self::span or self::p or self::small or self::label]" +
                "[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'dependents') " +
                "and contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'required')]";
        try {
            WebElement form = firstNameTextBox.findElement(By.xpath("ancestor::form"));
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .presenceOfNestedElementLocatedBy(form, By.xpath(xp)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickUpdate() {
        click(updateButton);
    }
}
