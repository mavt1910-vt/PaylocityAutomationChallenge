package UI.components;

import UI.core.EmployeeTableColumn;
import UI.core.BaseUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.math.BigDecimal;

public class EmployeesTableComponent extends BaseUI {

    @FindBy(id = "employeesTable")
    private WebElement table;

    public EmployeesTableComponent() {
        super();
        PageFactory.initElements(driver, this);
    }

    public void waitUntilRowPresent(String firstName, String lastName) {
        rowByName(firstName, lastName);
    }

    private WebElement rowByName(String firstName, String lastName) {
        waitUntilVisible(table);

        String xpCorrect = String.format(
                ".//tbody/tr[td[%d][normalize-space(text())='%s'] and td[%d][normalize-space(text())='%s']]",
                EmployeeTableColumn.LAST_NAME.getIndex(), lastName,
                EmployeeTableColumn.FIRST_NAME.getIndex(), firstName
        );

        String xpSwapped = String.format(
                ".//tbody/tr[td[%d][normalize-space(text())='%s'] and td[%d][normalize-space(text())='%s']]",
                EmployeeTableColumn.LAST_NAME.getIndex(), firstName,
                EmployeeTableColumn.FIRST_NAME.getIndex(), lastName
        );

        try {
            return wait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions
                            .presenceOfNestedElementLocatedBy(table, By.xpath(xpCorrect))
            );
        } catch (org.openqa.selenium.TimeoutException ignored) {
            return wait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions
                            .presenceOfNestedElementLocatedBy(table, By.xpath(xpSwapped))
            );
        }
    }

    private WebElement cell(WebElement row, EmployeeTableColumn col) {
        return row.findElement(By.xpath("./td[" + col.getIndex() + "]"));
    }

    private BigDecimal toMoney(String s) {
        if (s == null || s.isBlank()) return null;
        return new BigDecimal(s.trim());
    }

    public String getId(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return cell(r, EmployeeTableColumn.ID).getText().trim();
    }

    public BigDecimal getGrossPay(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return toMoney(cell(r, EmployeeTableColumn.GROSS_PAY).getText());
    }

    public BigDecimal getBenefitsCost(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return toMoney(cell(r, EmployeeTableColumn.BENEFITS_COST).getText());
    }

    public BigDecimal getNetPay(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return toMoney(cell(r, EmployeeTableColumn.NET_PAY).getText());
    }

    public int getDependents(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return Integer.parseInt(cell(r, EmployeeTableColumn.DEPENDENTS).getText().trim());
    }

    public BigDecimal getSalary(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return toMoney(cell(r, EmployeeTableColumn.SALARY).getText());
    }

    public String getFirstNameText(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return cell(r, UI.core.EmployeeTableColumn.FIRST_NAME).getText().trim();
    }

    public String getLastNameText(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        return cell(r, UI.core.EmployeeTableColumn.LAST_NAME).getText().trim();
    }

    public void clickEdit(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        WebElement editIcon = cell(r, EmployeeTableColumn.ACTIONS)
                .findElement(By.cssSelector("i.fa-edit"));
        click(editIcon);
    }

    public void clickDelete(String firstName, String lastName) {
        WebElement r = rowByName(firstName, lastName);
        WebElement delIcon = cell(r, EmployeeTableColumn.ACTIONS)
                .findElement(By.cssSelector("i.fa-times"));
        click(delIcon);
    }

    private String rowXpathAnyOrder(String firstName, String lastName) {
        return String.format(
                ".//tbody/tr[ (td[%d][normalize-space(text())='%s'] and td[%d][normalize-space(text())='%s'])" +
                        " or (td[%d][normalize-space(text())='%s'] and td[%d][normalize-space(text())='%s']) ]",
                EmployeeTableColumn.LAST_NAME.getIndex(), lastName,
                EmployeeTableColumn.FIRST_NAME.getIndex(), firstName,
                EmployeeTableColumn.LAST_NAME.getIndex(), firstName,
                EmployeeTableColumn.FIRST_NAME.getIndex(), lastName
        );
    }

    public boolean rowExists(String firstName, String lastName) {
        waitUntilVisible(table);
        String xp = rowXpathAnyOrder(firstName, lastName);
        return !table.findElements(By.xpath(xp)).isEmpty();
    }

    public boolean waitUntilRowAbsent(String firstName, String lastName) {
        String xp = rowXpathAnyOrder(firstName, lastName);
        try {
            return wait.until(d -> table.findElements(By.xpath(xp)).isEmpty());
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isDeleteActionVisible(String firstName, String lastName) {
        try {
            waitUntilVisible(table);

            String xpRow = String.format(
                    ".//tbody/tr[(td[%d][normalize-space(text())='%s'] and td[%d][normalize-space(text())='%s'])" +
                            " or (td[%d][normalize-space(text())='%s'] and td[%d][normalize-space(text())='%s'])]",
                    EmployeeTableColumn.LAST_NAME.getIndex(), lastName,
                    EmployeeTableColumn.FIRST_NAME.getIndex(), firstName,
                    EmployeeTableColumn.LAST_NAME.getIndex(), firstName,
                    EmployeeTableColumn.FIRST_NAME.getIndex(), lastName
            );
            java.util.List<org.openqa.selenium.WebElement> rows = table.findElements(org.openqa.selenium.By.xpath(xpRow));
            if (rows.isEmpty()) return false;

            org.openqa.selenium.WebElement r = rows.get(0);
            return !r.findElements(org.openqa.selenium.By.cssSelector("td:nth-child("
                    + EmployeeTableColumn.ACTIONS.getIndex() + ") i.fa-times")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

}
