package API.services;

import API.core.ApiHelper;
import API.models.employees.EmployeeCreateRequest;
import API.models.employees.EmployeeUpdateRequest;
import API.responses.employees.EmployeeResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.List;

public class EmployeeService {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static List<EmployeeResponse> getAll() {
        Response resp = ApiHelper.get("/api/Employees");
        try {
            return MAPPER.readValue(resp.asByteArray(),
                    new TypeReference<List<EmployeeResponse>>() {
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to map employees list", e);
        }
    }

    public static EmployeeResponse getById(String id) {
        Response resp = ApiHelper.get("/api/Employees/" + id);
        try {
            return MAPPER.readValue(resp.asByteArray(), EmployeeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map employee by id", e);
        }
    }

    public static void deleteById(String id) {
        if (id == null || id.isBlank()) {
            Common.LogHelper.warn("DeleteById called with null/blank id, skipping");
            return;
        }
        API.core.ApiHelper.delete("/api/Employees/" + id);
        Common.LogHelper.info("Deleted employee with id=" + id);
    }

    public static void deleteAll() {
        List<EmployeeResponse> employees = getAll();
        if (employees == null || employees.isEmpty()) {
            Common.LogHelper.info("No employees to delete.");
            return;
        }

        employees.forEach(emp -> deleteById(emp.getId()));
        Common.LogHelper.info("All employees deleted. Count: " + employees.size());
    }

    public static EmployeeResponse update(EmployeeUpdateRequest body) {
        if (body == null || body.getId() == null || body.getId().isBlank()) {
            Common.LogHelper.warn("Update called with null/invalid body or id, skipping");
            return null;
        }

        Response resp = API.core.ApiHelper.put("/api/Employees", body);
        try {
            return MAPPER.readValue(resp.asByteArray(), EmployeeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map update employee response", e);
        }
    }

    public static EmployeeResponse create(EmployeeCreateRequest body) {
        if (body == null) {
            Common.LogHelper.warn("Create called with null body, skipping");
            return null;
        }
        io.restassured.response.Response resp = API.core.ApiHelper.post("/api/Employees", body);
        try {
            return MAPPER.readValue(resp.asByteArray(), EmployeeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map create employee response", e);
        }
    }

    public static Response getAllRaw(boolean withAuth) {
        return withAuth
                ? API.core.ApiHelper.get("/api/Employees")
                : API.core.ApiHelper.getNoAuth("/api/Employees");
    }

}
