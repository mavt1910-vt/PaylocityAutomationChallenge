package UI.pages;

import UI.components.DeleteConfirmationComponent;
import UI.components.EmployeeFormComponent;
import UI.components.EmployeesTableComponent;
import UI.core.BaseUI;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BenefitsDashboardPage extends BaseUI {
    public final EmployeeFormComponent employeeForm;
    public final EmployeesTableComponent employeesTable;
    public final DeleteConfirmationComponent deleteConfirmation;

    @FindBy(id = "add")
    private WebElement addEmployeeButton;

    public BenefitsDashboardPage() {
        super();
        employeeForm = new EmployeeFormComponent();
        employeesTable = new EmployeesTableComponent();
        deleteConfirmation = new DeleteConfirmationComponent();
    }

    public EmployeeFormComponent clickAddEmployee() {
        click(addEmployeeButton);
        return employeeForm;
    }
}
