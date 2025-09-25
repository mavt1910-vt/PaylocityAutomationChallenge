package API.models.employees;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeUpdateRequest {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private Integer dependants;
    private Integer salary;

    public EmployeeUpdateRequest() {}

    public EmployeeUpdateRequest(String id, String username, String firstName, String lastName, Integer dependants, Integer salary) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dependants = dependants;
        this.salary = salary;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Integer getDependants() { return dependants; }
    public Integer getSalary() { return salary; }

    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDependants(Integer dependants) { this.dependants = dependants; }
    public void setSalary(Integer salary) { this.salary = salary; }
}