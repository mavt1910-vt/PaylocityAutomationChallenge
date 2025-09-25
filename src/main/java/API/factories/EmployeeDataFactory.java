package API.factories;

import API.models.employees.EmployeeCreateRequest;
import API.models.employees.EmployeeUpdateRequest;
import API.responses.employees.EmployeeResponse;
import Common.LogHelper;
import UI.core.ConfigManager;
import com.github.javafaker.Faker;

import java.util.Locale;

public final class EmployeeDataFactory {
    private static final Faker faker = new Faker(new Locale("en-US"));

    private EmployeeDataFactory() {
    }

    public static EmployeeCreateRequest createDefault() {
        String user = ConfigManager.user();
        String first = faker.name().firstName();
        String last = faker.name().lastName();
        int dependants = faker.number().numberBetween(0, 5);
        int salary = faker.number().numberBetween(30000, 150000);
        EmployeeCreateRequest body = new EmployeeCreateRequest(user, first, last, dependants, salary);
        LogHelper.pretty(body);
        return body;
    }

    public static EmployeeCreateRequest create(String firstName, String lastName, Integer dependants, Integer salary) {
        String user = ConfigManager.user();
        EmployeeCreateRequest body = new EmployeeCreateRequest(user, firstName, lastName, dependants, salary);
        LogHelper.pretty(body);
        return body;
    }

    public static EmployeeUpdateRequest update(String id) {
        String user = ConfigManager.user();
        String first = faker.name().firstName();
        String last = faker.name().lastName();
        int dependants = faker.number().numberBetween(0, 10);
        int salary = faker.number().numberBetween(30000, 200000);
        EmployeeUpdateRequest body = new EmployeeUpdateRequest(id, user, first, last, dependants, salary);
        LogHelper.pretty(body);
        return body;
    }

    public static EmployeeUpdateRequest updateFromExisting(EmployeeResponse existing) {
        String user = ConfigManager.user();
        String first = faker.name().firstName();
        String last = faker.name().lastName();
        Integer newDep = faker.number().numberBetween(0, 10);
        Integer newSal = faker.number().numberBetween(30000, 200000);
        EmployeeUpdateRequest body = new EmployeeUpdateRequest(
                existing.getId(),
                user,
                first,
                last,
                newDep,
                newSal
        );
        LogHelper.pretty(body);
        return body;
    }

    public static EmployeeCreateRequest createMinimal(String first, String last, Integer salary) {
        String user = ConfigManager.user();
        EmployeeCreateRequest body = new EmployeeCreateRequest();
        body.setUsername(user);
        body.setFirstName(first);
        body.setLastName(last);
        body.setSalary(salary);
        LogHelper.pretty(body);
        return body;
    }

    public static EmployeeCreateRequest createNamesOnly(String first, String last) {
        String user = ConfigManager.user();
        EmployeeCreateRequest body = new EmployeeCreateRequest();
        body.setUsername(user);
        body.setFirstName(first);
        body.setLastName(last);
        LogHelper.pretty(body);
        return body;
    }
}
