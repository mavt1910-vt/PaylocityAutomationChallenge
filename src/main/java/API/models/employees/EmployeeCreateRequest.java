package API.models.employees;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeCreateRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Integer dependants;
    private Integer salary;

    public EmployeeCreateRequest() {}

    public EmployeeCreateRequest(String username, String firstName, String lastName, Integer dependants, Integer salary) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dependants = dependants;
        this.salary = salary;
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Integer getDependants() { return dependants; }
    public Integer getSalary() { return salary; }

    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDependants(Integer dependants) { this.dependants = dependants; }
    public void setSalary(Integer salary) { this.salary = salary; }
}