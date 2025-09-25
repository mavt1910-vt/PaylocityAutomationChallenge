package UI;

import API.services.EmployeeService;
import Common.LogHelper;
import UI.components.DeleteConfirmationComponent;
import UI.components.EmployeeFormComponent;
import UI.pages.BenefitsDashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import UI.pages.LoginUI;

import java.math.BigDecimal;

public class EmployeesUITest extends BaseTest {

    @DataProvider(name = "dependentsCases")
    public Object[][] dependentsCases() {
        return new Object[][]{
                {"Tony", "Stark", 0, new BigDecimal("1000.00")},
                {"Peter", "Parker", 1, new BigDecimal("1500.00")},
                {"Bruce", "Banner", 2, new BigDecimal("2000.00")}
        };
    }

    @BeforeMethod
    public void initialCleanUp() {
        EmployeeService.deleteAll();
    }

    @Test(dataProvider = "dependentsCases", testName = "AT-01/AT-02 Add employee with dependents check annual net")
    public void annualNet_shouldMatchSalaryMinusBenefits(String first, String last, int dependents, BigDecimal expectedAnnualBenefits) {
        final BigDecimal ANNUAL_SALARY = new BigDecimal("52000.00");
        final BigDecimal EXPECTED_ANNUAL_NET = ANNUAL_SALARY.subtract(expectedAnnualBenefits);

        BenefitsDashboardPage benefitsDashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, String.valueOf(dependents));

        benefitsDashboardPage.employeesTable.waitUntilRowPresent(first, last);

        BigDecimal uiNetPerPeriod = benefitsDashboardPage.employeesTable.getNetPay(first, last);
        BigDecimal uiAnnualNet = uiNetPerPeriod.multiply(new BigDecimal("26"))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        LogHelper.info("UI annual net: " + uiAnnualNet + " vs expected: " + EXPECTED_ANNUAL_NET
                + " (dependents=" + dependents + ", annual benefits=" + expectedAnnualBenefits + ")");

        Assert.assertEquals(uiAnnualNet, EXPECTED_ANNUAL_NET,
                "Annual net should equal salary - annual benefits");
    }

    @Test(testName = "AT-03 Create an employee with first name empty")
    public void emptyFirstName_shouldShowValidationError() {
        BenefitsDashboardPage dashboard = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin();

        EmployeeFormComponent form = dashboard
                .clickAddEmployee()
                .typeFirstName("")
                .typeLastName("Stark")
                .typeDependents("1");

        form.clickAdd();

        Assert.assertTrue(form.isFirstNameErrorVisible(),
                "Should display validation error: 'First Name is required'");
    }

    @Test (testName = "AT-04 Crate an employee with 32 dependents")
    public void maxDependentsAccepted_32() {
        final String first = "Max";
        final String last = "Deps32";
        final int dependents = 32;

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, String.valueOf(dependents));


        dashboardPage.employeesTable.waitUntilRowPresent(first, last);

        int uiDependents = dashboardPage.employeesTable.getDependents(first, last);
        LogHelper.info("Employee added with dependents=" + uiDependents);

        Assert.assertEquals(uiDependents, dependents, "Employee should be created with 32 dependents");
    }

    @Test(testName = "AT-05 Add employee with duplicated name")
    public void duplicateEmployee_sameFirstAndLast_shouldShowError() {
        final String first = "Steve";
        final String last = "Rogers";
        final int dependents = 1;

        BenefitsDashboardPage dashboard = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin();

        dashboard
                .clickAddEmployee()
                .addEmployee(first, last, String.valueOf(dependents));
        dashboard.employeesTable.waitUntilRowPresent(first, last);

        EmployeeFormComponent form = dashboard
                .clickAddEmployee()
                .typeFirstName(first)
                .typeLastName(last)
                .typeDependents(String.valueOf(dependents));

        form.clickAdd();

        boolean duplicateShown = form.isDuplicateEmployeeErrorVisible();
        LogHelper.info("Duplicate error shown? " + duplicateShown);

        Assert.assertTrue(duplicateShown, "Should display duplicate employee validation error.");
    }

    @Test(testName = "AT-06 Create an employee with >50 characters in first name and last name")
    public void veryLongNames_shouldBeRejectedWithError() {
        final String first = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        final String last = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";

        BenefitsDashboardPage dashboard = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin();

        EmployeeFormComponent form = dashboard
                .clickAddEmployee()
                .typeFirstName(first)
                .typeLastName(last)
                .typeDependents("1");

        form.clickAdd();

        boolean tooLongShown = form.isNameTooLongErrorVisible();
        LogHelper.info("Too-long name error shown? " + tooLongShown);

        Assert.assertTrue(tooLongShown,
                "Should display length validation error: 'First/Last name must be 50 characters or fewer.'");
    }

    @Test(testName = "Create an employee with newline and tab in employee name")
    public void controlCharactersInNames_shouldShowInvalidCharsError() {
        final String firstRaw = "P\\nParker";
        final String lastRaw = "T\\tStark";
        final String deps = "1";

        BenefitsDashboardPage dashboard = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin();

        UI.components.EmployeeFormComponent form = dashboard
                .clickAddEmployee()
                .typeFirstNameRaw(firstRaw)
                .typeLastNameRaw(lastRaw)
                .typeDependents(deps);

        Common.LogHelper.info("Submitting employee with raw control sequences: first='" + firstRaw + "', last='" + lastRaw + "'");
        form.clickAdd();

        boolean invalidShown = form.isInvalidCharsErrorVisible();
        Common.LogHelper.info("Invalid-characters validation shown? " + invalidShown);

        Assert.assertTrue(invalidShown, "Expected validation error for invalid characters in names.");
    }

    @Test(testName = "AT-08 Add employee with quotes and special punctuation")
    public void quotes_and_punctuation_should_display_correctly() {
        final String first = "O’Conner";
        final String last = "Bryan \"The Family first\"";
        final String deps = "0";

        BenefitsDashboardPage dashboard = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, String.valueOf(deps));

        dashboard.employeesTable.waitUntilRowPresent(first, last);

        String uiFirst = dashboard.employeesTable.getFirstNameText(first, last);
        String uiLast = dashboard.employeesTable.getLastNameText(first, last);

        LogHelper.info("UI First='" + uiFirst + "', UI Last='" + uiLast + "'");

        Assert.assertEquals(uiFirst, first, "First name should render exactly as entered.");
        Assert.assertEquals(uiLast, last, "Last name should render exactly as entered.");

        Assert.assertFalse(uiFirst.contains("<") || uiFirst.contains(">"), "First name must not contain HTML.");
        Assert.assertFalse(uiLast.contains("<") || uiLast.contains(">"), "Last name must not contain HTML.");
    }

    @Test(testName = "AT-09 Save button is enabled when fields are empty")
    public void addButton_shouldRemainDisabled_withInvalidData() {
        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee("", "", "");

        boolean enabled = dashboardPage.employeeForm.isAddEnabled();
        LogHelper.info("Add/Save button enabled? " + enabled);

        Assert.assertFalse(enabled, "Save button should remain disabled when required fields are blank.");
    }

    @Test(testName = "AT-10 Add employee with no numeric characters")
    public void dependents_shouldRejectNonNumeric() {
        final String first = "Peter";
        final String last = "Parker";
        final String deps = "22z1";

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, deps);

        LogHelper.info("Submitting with non-numeric dependents: " + deps);
        dashboardPage.employeeForm.clickAdd();

        boolean errorShown = dashboardPage.employeeForm.isDependentsNumericErrorVisible();
        LogHelper.info("Dependents numeric validation shown? " + errorShown);

        Assert.assertTrue(errorShown, "Should reject non-numeric dependents; expect numeric/integer validation error.");
    }

    @Test(testName = "AT-11 First and last name swapped in the employee table")
    public void fieldMapping_shouldDisplayCorrectly() {
        final String first = "Natasha";
        final String last = "Romanoff";
        final String deps = "0";

        BenefitsDashboardPage dashboard = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, deps);

        dashboard.employeesTable.waitUntilRowPresent(first, last);

        String uiFirst = dashboard.employeesTable.getFirstNameText(first, last);
        String uiLast = dashboard.employeesTable.getLastNameText(first, last);

        LogHelper.info("UI First='" + uiFirst + "', UI Last='" + uiLast + "'");

        Assert.assertEquals(uiFirst, first, "First name should render exactly as entered (BUG: inverted mapping).");
        Assert.assertEquals(uiLast, last, "Last name should render exactly as entered (BUG: inverted mapping).");
    }

    @Test(testName = "AT-12 Updates to employees should have confirmation")
    public void update_shouldShowConfirmationDialog() {
        final String first = "Steven";
        final String last = "Strange";

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, "1");

        dashboardPage.employeesTable.waitUntilRowPresent(first, last);

        dashboardPage.employeesTable.clickEdit(first, last);


        dashboardPage.employeeForm.typeFirstName("StevenUpdated")
                .typeLastName("StrangeUpdated");

        LogHelper.info("Clicking Update to save edited employee...");
        dashboardPage.employeeForm.clickUpdate();

        boolean confirmationShown = dashboardPage.employeeForm.isUpdateConfirmationVisible();
        LogHelper.info("Update confirmation visible? " + confirmationShown);

        Assert.assertTrue(confirmationShown, "A confirmation dialog/toast should appear after updating an employee.");
    }

    @Test(testName = "AT-13 Add employee with more 32 dependents should display error message")
    public void dependentsGreaterThan32_shouldShowRangeError() {
        final String first = "Steve";
        final String last = "Rogers";
        final int deps = 33;

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, String.valueOf(deps));

        LogHelper.info("Submitting with dependents=" + deps);
        dashboardPage.employeeForm.clickAdd();

        boolean errorShown = dashboardPage.employeeForm.isDependentsRangeErrorVisible();
        LogHelper.info("Dependents range error shown? " + errorShown);

        Assert.assertTrue(errorShown,
                "Should show validation error: 'Dependents must be between 0 and 32.'");
    }

    @Test(testName = "AT-14 Edit an employee's name and the benefit cost calculations not change")
    public void editEmployeeName_shouldUpdateNameAndKeepIdAndCalcs() {
        final String firstInit = "Wanda";
        final String lastInit = "Maximoff";
        final String deps = "1";
        final String firstUpdated = "Natasha";

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(firstInit, lastInit, deps);

        dashboardPage.employeesTable.waitUntilRowPresent(firstInit, lastInit);

        String idBefore = dashboardPage.employeesTable.getId(firstInit, lastInit);
        BigDecimal grossBefore = dashboardPage.employeesTable.getGrossPay(firstInit, lastInit);
        BigDecimal benefitsBefore = dashboardPage.employeesTable.getBenefitsCost(firstInit, lastInit);
        BigDecimal netBefore = dashboardPage.employeesTable.getNetPay(firstInit, lastInit);

        LogHelper.info("Before update -> ID=" + idBefore + ", gross=" + grossBefore
                + ", benefits=" + benefitsBefore + ", net=" + netBefore);

        dashboardPage.employeesTable.clickEdit(firstInit, lastInit);

        dashboardPage.employeeForm
                .typeFirstName(firstUpdated)
                .typeLastName(lastInit)
                .clickUpdate();

        dashboardPage.employeesTable.waitUntilRowPresent(firstUpdated, lastInit);

        String idAfter = dashboardPage.employeesTable.getId(firstUpdated, lastInit);
        BigDecimal grossAfter = dashboardPage.employeesTable.getGrossPay(firstUpdated, lastInit);
        BigDecimal benefitsAfter = dashboardPage.employeesTable.getBenefitsCost(firstUpdated, lastInit);
        BigDecimal netAfter = dashboardPage.employeesTable.getNetPay(firstUpdated, lastInit);

        String uiFirst = dashboardPage.employeesTable.getFirstNameText(firstUpdated, lastInit);
        String uiLast = dashboardPage.employeesTable.getLastNameText(firstUpdated, lastInit);

        LogHelper.info("After update  -> ID=" + idAfter + ", gross=" + grossAfter
                + ", benefits=" + benefitsAfter + ", net=" + netAfter
                + " | UI First='" + uiFirst + "', UI Last='" + uiLast + "'");

        Assert.assertEquals(idAfter, idBefore, "Record ID should remain the same after name edit.");
        Assert.assertEquals(grossAfter, grossBefore, "Gross Pay should remain unchanged.");
        Assert.assertEquals(benefitsAfter, benefitsBefore, "Benefits Cost should remain unchanged.");
        Assert.assertEquals(netAfter, netBefore, "Net Pay should remain unchanged.");

        Assert.assertEquals(uiFirst, firstUpdated, "First Name should be updated to 'Natasha'.");
        Assert.assertEquals(uiLast, lastInit, "Last Name should remain 'Maximoff'.");
    }

    @Test(testName = "AT-15 Delete an employee")
    public void deleteEmployee_shouldDisappearAndNotReappear() {
        final String first = "Peter";
        final String last = "Parker";

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, "0");

        dashboardPage.employeesTable.waitUntilRowPresent(first, last);

        dashboardPage.employeesTable.clickDelete(first, last);


        LogHelper.info("Delete modal -> ID=" + dashboardPage.deleteConfirmation.getDeleteId()
                + " | FirstName=" + dashboardPage.deleteConfirmation.getFirstName()
                + " | LastName=" + dashboardPage.deleteConfirmation.getLastName());

        dashboardPage.deleteConfirmation.confirmDelete();

        boolean gone = dashboardPage.employeesTable.waitUntilRowAbsent(first, last);
        LogHelper.info("Row absent after delete? " + gone);
        Assert.assertTrue(gone, "Employee should be removed from the table after deletion.");

        boolean stillGone = !dashboardPage.employeesTable.rowExists(first, last);
        LogHelper.info("Row still absent after refresh? " + stillGone);
        Assert.assertTrue(stillGone, "Employee should not reappear after page refresh.");
    }

    @Test(testName = "AT-16 Validate second delete on the same employee")
    public void secondDelete_shouldNotBePossible() {
        final String first = "Peter";
        final String last = "Parker";

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, "0");

        dashboardPage.employeesTable.waitUntilRowPresent(first, last);

        dashboardPage.employeesTable.clickDelete(first, last);
        DeleteConfirmationComponent modal = new DeleteConfirmationComponent();
        modal.confirmDelete();

        boolean gone = dashboardPage.employeesTable.waitUntilRowAbsent(first, last);
        LogHelper.info("Row absent after first delete? " + gone);
        Assert.assertTrue(gone, "Employee should be removed after first deletion.");

        boolean deleteVisible = dashboardPage.employeesTable.isDeleteActionVisible(first, last);
        LogHelper.info("Delete action visible for already-deleted employee? " + deleteVisible);

        Assert.assertFalse(deleteVisible, "UI should not allow deleting again; delete action must not be present.");
    }

    @Test(testName = "AT-17 Edit the number of dependents and recalculates benefits cost")
    public void editDependents_shouldIncreaseBenefits_andDecreaseNet() {
        final String first = "Peter";
        final String last = "Parker";

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, "0");

        dashboardPage.employeesTable.waitUntilRowPresent(first, last);

        BigDecimal benefitsBefore = dashboardPage.employeesTable.getBenefitsCost(first, last);
        BigDecimal netBefore = dashboardPage.employeesTable.getNetPay(first, last);
        int depsBefore = dashboardPage.employeesTable.getDependents(first, last);

        LogHelper.info("Before update -> deps=" + depsBefore
                + ", benefits=" + benefitsBefore + ", net=" + netBefore);

        dashboardPage.employeesTable.clickEdit(first, last);

        dashboardPage.employeeForm
                .typeDependents("1")
                .clickUpdate();

        dashboardPage.employeesTable.waitUntilRowPresent(first, last);

        BigDecimal benefitsAfter = dashboardPage.employeesTable.getBenefitsCost(first, last);
        BigDecimal netAfter = dashboardPage.employeesTable.getNetPay(first, last);
        int depsAfter = dashboardPage.employeesTable.getDependents(first, last);

        LogHelper.info("After update  -> deps=" + depsAfter
                + ", benefits=" + benefitsAfter + ", net=" + netAfter);

        Assert.assertEquals(depsAfter, 1, "Dependents should update to 1.");
        Assert.assertTrue(benefitsAfter.compareTo(benefitsBefore) > 0,
                "Benefits Cost should increase after adding a dependent.");
        Assert.assertTrue(netAfter.compareTo(netBefore) < 0,
                "Net Pay should decrease after adding a dependent.");
    }

    @Test(testName = "Add employee with empty dependents should display a error message")
    public void emptyDependents_shouldShowRequiredError_andNotInsert() {
        final String first = "Natasha";
        final String last = "Romanoff";

        BenefitsDashboardPage dashboardPage = new LoginUI()
                .open(url)
                .typeUsername(user)
                .typePassword(password)
                .clickLogin()
                .clickAddEmployee()
                .addEmployee(first, last, "");

        LogHelper.info("Submitted employee with empty dependents field");

        boolean errorShown = dashboardPage.employeeForm.isDependentsRequiredErrorVisible();
        LogHelper.info("Dependents required error visible? " + errorShown);

        Assert.assertTrue(errorShown, "Should show validation error: 'Dependents is required.'");
    }

}
