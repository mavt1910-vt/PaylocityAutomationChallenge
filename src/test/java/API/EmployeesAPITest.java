package API;

import API.core.ApiHelper;
import API.factories.EmployeeDataFactory;
import API.models.employees.EmployeeCreateRequest;
import API.models.employees.EmployeeUpdateRequest;
import API.responses.employees.EmployeeResponse;
import API.services.EmployeeService;
import Common.LogHelper;
import UI.core.ConfigManager;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.UUID.randomUUID;

public class EmployeesAPITest {

    @BeforeMethod
    public void cleanAndSeed() {
        EmployeeService.deleteAll();
    }

    @Test(testName = "API01 Get all employees")
    public void getAllEmployees_shouldReturnList() {
        EmployeeCreateRequest body = EmployeeDataFactory.createDefault();
        EmployeeService.create(body);

        List<EmployeeResponse> employees = EmployeeService.getAll();

        Assert.assertFalse(employees.isEmpty(), "Employee list should not be empty");

        EmployeeResponse first = employees.get(0);
        Assert.assertNotNull(first.getId(), "Employee id should not be null");
        Assert.assertNotNull(first.getFirstName(), "First name should not be null");
        Assert.assertNotNull(first.getLastName(), "Last name should not be null");
        Assert.assertNotNull(first.getDependants(), "Dependants should not be null");
        Assert.assertNotNull(first.getSalary(), "Salary should not be null");
        Assert.assertNotNull(first.getGross(), "Gross should not be null");
        Assert.assertNotNull(first.getBenefitsCost(), "Benefits cost should not be null");
        Assert.assertNotNull(first.getNet(), "Net should not be null");

        LogHelper.info("Fetched " + employees.size() + " employees. First: " +
                first.getFirstName() + " " + first.getLastName() + ", id=" + first.getId());
    }

    @Test(testName = "API02 Get employees without logging in ")
    public void getAll_withoutAuth_shouldReturn401() {
        Response resp = EmployeeService.getAllRaw(false);
        LogHelper.info("Unauthorized GET /api/Employees -> status: " + resp.statusCode());
        Assert.assertEquals(resp.statusCode(), 401, "Should return 401 Unauthorized when token is missing");
    }

    @Test(testName = "API03 Create employee with only the required info")
    public void createEmployee_withMinimalFields_shouldSucceed() {
        EmployeeCreateRequest body = EmployeeDataFactory.createMinimal("Peter", "Parker", 60000);

        EmployeeResponse resp = EmployeeService.create(body);

        LogHelper.info("Created employee id=" + resp.getId()
                + " first=" + resp.getFirstName()
                + " last=" + resp.getLastName()
                + " dependents=" + resp.getDependants());

        Assert.assertNotNull(resp.getId(), "Employee ID should be generated");
        Assert.assertEquals(resp.getFirstName(), "Peter");
        Assert.assertEquals(resp.getLastName(), "Parker");
        Assert.assertEquals(resp.getDependants(), Integer.valueOf(0),
                "Dependants should default to 0");
        Assert.assertNotNull(resp.getBenefitsCost(), "Benefits cost should be calculated");
    }

    @Test(testName = "API04 Crate employee with two dependents and check costs are correctly calculate")
    public void create_withTwoDependents_shouldCalculateBenefitsCorrectly() {
        EmployeeCreateRequest body = new EmployeeCreateRequest();
        body.setUsername(ConfigManager.user());
        body.setFirstName("Steve");
        body.setLastName("Rogers");
        body.setDependants(2);

        EmployeeResponse resp = EmployeeService.create(body);

        LogHelper.info("Created id=" + resp.getId()
                + " first=" + resp.getFirstName()
                + " last=" + resp.getLastName()
                + " dependants=" + resp.getDependants()
                + " salary=" + resp.getSalary()
                + " gross=" + resp.getGross()
                + " benefitsCost=" + resp.getBenefitsCost()
                + " net=" + resp.getNet());

        BigDecimal expectedAnnual = new BigDecimal("2000.00");
        BigDecimal expectedPerPay = new BigDecimal("76.92");
        BigDecimal expectedNet = new BigDecimal("1923.08");

        BigDecimal apiPerPay = BigDecimal.valueOf(resp.getBenefitsCost()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal apiAnnual = BigDecimal.valueOf(resp.getBenefitsCost())
                .multiply(new BigDecimal("26"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal apiNet = BigDecimal.valueOf(resp.getNet()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal apiGross = BigDecimal.valueOf(resp.getGross()).setScale(2, RoundingMode.HALF_UP);

        LogHelper.info("PerPay API=" + apiPerPay + " vs expected=" + expectedPerPay);
        LogHelper.info("Annual  API=" + apiAnnual + " vs expected=" + expectedAnnual);
        LogHelper.info("Net     API=" + apiNet + " vs expected=" + expectedNet + " (gross=" + apiGross + ")");

        Assert.assertEquals(apiPerPay, expectedPerPay, "Per-paycheck benefits should be 76.92");
        Assert.assertEquals(apiAnnual, expectedAnnual, "Annual benefits should be 2000.00");
        Assert.assertEquals(apiNet, expectedNet, "Net per paycheck should be 1923.08");
    }

    @Test(testName = "API05 Add employee with missing required fields")
    public void createEmployee_missingFirstName_shouldReturn400() {
        EmployeeCreateRequest body = new EmployeeCreateRequest();
        body.setUsername(ConfigManager.user());
        body.setFirstName("");
        body.setLastName("Parker");
        body.setDependants(0);
        body.setSalary(60000);

        Response resp = ApiHelper.post("/api/Employees", body);

        Assert.assertEquals(resp.statusCode(), 400, "Should return 400 Bad Request");
        String error = resp.jsonPath().getString("[0].errorMessage");
        Assert.assertTrue(error.contains("FirstName field is required"),
                "Error message should mention FirstName is required. Actual: " + error);
    }

    @Test(testName = "API06 Add employee with over 32 dependents")
    public void createEmployee_dependantsOver32_shouldReturn400() {
        EmployeeCreateRequest body = EmployeeDataFactory.create("Peter", "Parker", 33, 60000);

        Response resp = ApiHelper.post("/api/Employees", body);

        Assert.assertEquals(resp.statusCode(), 400, "Should return 400 Bad Request");
        String error = resp.jsonPath().getString("[0].errorMessage");
        Assert.assertTrue(error.contains("Dependants must be between 0 and 32")
                        || error.contains("Dependants must be between 0 and 32.")
                        || error.contains("The field Dependants must be between 0 and 32."),
                "Error message should state dependants must be between 0 and 32. Actual: " + error);
    }

    @Test(testName = "API07 Create employee with negative salary")
    public void createEmployee_negativeSalary_shouldReturn400() {
        EmployeeCreateRequest body = EmployeeDataFactory.create("Peter", "Parker", 0, -1);

        Response resp = ApiHelper.post("/api/Employees", body);

        Assert.assertEquals(resp.statusCode(), 400, "Should return 400 Bad Request for negative salary");
        String error = resp.jsonPath().getString("[0].errorMessage");
        Assert.assertTrue(
                error != null && error.toLowerCase().contains("salary"),
                "Expected clear validation error about salary. Actual: " + error
        );
    }

    @Test(testName = "API08 Add employee with unknown extra fields")
    public void createEmployee_withUnknownField_shouldReturn400() {

        Map<String, Object> body = new HashMap<>();
        body.put("username", ConfigManager.user());
        body.put("firstName", "Steven");
        body.put("lastName", "Strange");
        body.put("dependants", 0);
        body.put("salary", 60000);
        body.put("test", "testing");

        Response resp = ApiHelper.post("/api/Employees", body);

        Assert.assertEquals(resp.statusCode(), 400, "Should return 400 Bad Request for unknown fields");
        String error = resp.jsonPath().getString("[0].errorMessage");
        Assert.assertTrue(
                error != null && error.toLowerCase().contains("unknown")
                        || error.toLowerCase().contains("not allowed"),
                "Expected error message about unknown property. Actual: " + error
        );
    }

    @Test(testName = "API09 Get employee by Id")
    public void getEmployeeById_shouldReturnCorrectEmployee() {
        EmployeeCreateRequest body = EmployeeDataFactory.createDefault();
        EmployeeResponse created = EmployeeService.create(body);

        EmployeeResponse fetched = EmployeeService.getById(created.getId());

        Assert.assertEquals(fetched.getId(), created.getId(), "Fetched ID must match created ID");
        Assert.assertEquals(fetched.getFirstName(), created.getFirstName(), "FirstName must match");
        Assert.assertEquals(fetched.getLastName(), created.getLastName(), "LastName must match");
        Assert.assertNotNull(fetched.getGross(), "Gross should not be null");
        Assert.assertNotNull(fetched.getBenefitsCost(), "Benefits cost should not be null");
        Assert.assertNotNull(fetched.getNet(), "Net should not be null");
    }

    @Test(testName = "API10 Get employee by non existent ID")
    public void getEmployeeByNonExistentId_shouldReturn404() {
        String randomId = randomUUID().toString();
        LogHelper.info(randomId);

        Response resp = ApiHelper.get("/api/Employees/" + randomId);

        Assert.assertEquals(resp.statusCode(), 404, "Should return 404 Not Found for non-existent ID");

        String error = resp.asString();
        Assert.assertTrue(error.toLowerCase().contains("not found") || error.toLowerCase().contains("no employee"),
                "Error message should clearly indicate not found. Actual: " + error);
    }

    @Test(testName = "API11 Get employee by invalid ID")
    public void getEmployeeByInvalidId_shouldReturn400() {
        String invalidId = "NOT-A-UUID";

        Response resp = ApiHelper.get("/api/Employees/" + invalidId);

        Assert.assertEquals(resp.statusCode(), 400, "Should return 400 Bad Request for invalid ID format");

        String error = resp.asString();
        Assert.assertTrue(error.toLowerCase().contains("invalid")
                        || error.toLowerCase().contains("bad request")
                        || error.toLowerCase().contains("id"),
                "Error message should clearly indicate invalid ID. Actual: " + error);
    }

    @Test(testName = "API12 Update employee to one dependent")
    public void updateEmployee_toOneDependent_shouldRecalculateBenefits() {
        EmployeeResponse created = EmployeeService.create(
                EmployeeDataFactory.create("Peter", "Parker", 0, 52000)
        );
        Assert.assertNotNull(created.getId(), "Created employee must have an ID");

        EmployeeUpdateRequest upd = new EmployeeUpdateRequest(
                created.getId(),
                ConfigManager.user(),
                created.getFirstName(),
                created.getLastName(),
                1,
                created.getSalary()
        );
        EmployeeResponse updated = EmployeeService.update(upd);

        BigDecimal perPay = BigDecimal.valueOf(updated.getBenefitsCost()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal annual = BigDecimal.valueOf(updated.getBenefitsCost()).multiply(new BigDecimal("26"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal gross = BigDecimal.valueOf(updated.getGross()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal net = BigDecimal.valueOf(updated.getNet()).setScale(2, RoundingMode.HALF_UP);

        Assert.assertEquals(perPay, new BigDecimal("57.69"), "Per paycheck benefits must be 57.69");
        Assert.assertEquals(annual, new BigDecimal("1500.00"), "Annual benefits must be 1500.00");
        Assert.assertEquals(net, gross.subtract(new BigDecimal("57.69")).setScale(2, RoundingMode.HALF_UP),
                "Net must be gross - benefits per paycheck");
    }

    @Test(testName = "API13 Edit a non existing employee")
    public void updateNonExistentEmployee_shouldReturn404() {
        EmployeeUpdateRequest upd = new EmployeeUpdateRequest(
                "00000000-0000-0000-0000-000000000000",
                "NoOne",
                "No",
                "One",
                1,
                60000
        );

        Response resp = ApiHelper.put("/api/Employees", upd);

        Assert.assertEquals(resp.statusCode(), 404,
                "Should return 404 Not Found when updating a non-existent employee ID");

        String error = resp.asString();
        Assert.assertTrue(error.toLowerCase().contains("not found")
                        || error.toLowerCase().contains("no employee"),
                "Error message should clearly indicate employee not found. Actual: " + error);
    }

    @Test(testName = "API14 Edit employee with invalid ID")
    public void updateEmployee_withInvalidUUID_shouldReturn400() {
        EmployeeUpdateRequest upd = new EmployeeUpdateRequest(
                "NOT-A-UUID",
                "X",
                "Y",
                "Z",
                1,
                60000
        );

        Response resp = ApiHelper.put("/api/Employees", upd);

        Assert.assertEquals(resp.statusCode(), 400,
                "Should return 400 Bad Request when ID is not a valid UUID");

        String error = resp.asString();
        Assert.assertTrue(error.toLowerCase().contains("invalid")
                        || error.toLowerCase().contains("bad request")
                        || error.toLowerCase().contains("uuid"),
                "Error message should clearly indicate invalid UUID. Actual: " + error);
    }

    @Test(testName = "API15 Update employee with tampered gross and net")
    public void updateEmployee_withTamperedGrossNet_shouldIgnoreClientValues() {
        EmployeeResponse created = EmployeeService.create(
                EmployeeDataFactory.create("Peter", "Parker", 0, 52000)
        );
        Assert.assertNotNull(created.getId(), "Created employee must have an ID");

        Map<String, Object> tampered = new HashMap<>();
        tampered.put("id", created.getId());
        tampered.put("username", ConfigManager.user());
        tampered.put("firstName", created.getFirstName());
        tampered.put("lastName", created.getLastName());
        tampered.put("dependants", 2);
        tampered.put("gross", 9999);
        tampered.put("net", 9999);

        Response resp = ApiHelper.put("/api/Employees", tampered);
        Assert.assertEquals(resp.statusCode(), 200, "Update should return 200 OK");

        EmployeeResponse updated = resp.as(EmployeeResponse.class);

        Assert.assertNotEquals(updated.getGross(), Double.valueOf(9999),
                "Gross should not accept tampered client value");
        Assert.assertNotEquals(updated.getNet(), Double.valueOf(9999),
                "Net should not accept tampered client value");
    }

    @Test(testName = "API16 Delete employee")
    public void deleteEmployee_shouldRemoveAndReturn404OnGet() {
        EmployeeResponse created = EmployeeService.create(
                EmployeeDataFactory.create("Delete", "Me", 0, 52000)
        );

        Response deleteResp = ApiHelper.delete("/api/Employees/" + created.getId());
        Assert.assertTrue(deleteResp.statusCode() == 200 || deleteResp.statusCode() == 204,
                "Delete should return 200 or 204. Actual: " + deleteResp.statusCode());

        Response getResp = ApiHelper.get("/api/Employees/" + created.getId());
        Assert.assertEquals(getResp.statusCode(), 404,
                "After delete, GET by id should return 404 Not Found");
    }

    @Test(testName = "API 17 Delete a non existent employee")
    public void deleteNonExistentEmployee_shouldReturn404() {
        String nonExistentId = java.util.UUID.randomUUID().toString();

        Response resp = ApiHelper.delete("/api/Employees/" + nonExistentId);

        Assert.assertEquals(resp.statusCode(), 404,
                "Should return 404 Not Found when deleting a non-existent employee ID");

        String error = resp.asString();
        Assert.assertTrue(error.toLowerCase().contains("not found")
                        || error.toLowerCase().contains("no employee"),
                "Error message should clearly indicate employee not found. Actual: " + error);
    }

    @Test(testName = "API18 Delete employee with invalid ID")
    public void deleteEmployee_withInvalidUUID_shouldReturn400() {
        String invalidId = "NOT-A-UUID";

        Response resp = ApiHelper.delete("/api/Employees/" + invalidId);

        Assert.assertEquals(resp.statusCode(), 400,
                "Should return 400 Bad Request when deleting with an invalid UUID");

        String error = resp.asString();
        Assert.assertTrue(error.toLowerCase().contains("invalid")
                        || error.toLowerCase().contains("bad request")
                        || error.toLowerCase().contains("uuid"),
                "Error message should clearly indicate invalid UUID. Actual: " + error);
    }

    @Test(testName = "API19 Create employee without content type")
    public void createEmployee_withoutContentType_shouldReturn415() {
        EmployeeCreateRequest body = EmployeeDataFactory.createNamesOnly("Miles", "Morales");

        Response resp = ApiHelper.postNoContentType("/api/Employees", body);

        Assert.assertEquals(resp.statusCode(), 415,
                "Should return 415 Unsupported Media Type when Content-Type header is missing");
    }

    @Test(testName = "API20 Create employee with decimal dependents")
    public void createEmployee_withDecimalDependants_shouldReturn400() {
        String body = "{"
                + "\"username\":\"" + ConfigManager.user() + "\","
                + "\"firstName\":\"Decimal\","
                + "\"lastName\":\"Dependents\","
                + "\"dependants\":1.6,"
                + "\"salary\":60000"
                + "}";

        Response resp = ApiHelper.postRawJson("/api/Employees", body);

        Assert.assertEquals(resp.statusCode(), 400,
                "Should return 400 Bad Request when dependants is decimal");
        Assert.assertTrue(resp.asString().toLowerCase().contains("dependants")
                        || resp.asString().toLowerCase().contains("integer"),
                "Error message should mention dependants must be integer. Actual: " + resp.asString());
    }

}
