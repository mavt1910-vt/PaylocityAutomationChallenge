package UI.core;

public enum EmployeeTableColumn {
    ID(1),
    LAST_NAME(2),
    FIRST_NAME(3),
    DEPENDENTS(4),
    SALARY(5),
    GROSS_PAY(6),
    BENEFITS_COST(7),
    NET_PAY(8),
    ACTIONS(9);

    private final int index;

    EmployeeTableColumn(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}