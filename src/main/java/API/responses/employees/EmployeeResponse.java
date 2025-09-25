package API.responses.employees;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeResponse {
    private String partitionKey;
    private String sortKey;
    private String username;
    private String id;
    private String firstName;
    private String lastName;
    private Integer dependants;
    private String expiration;
    private Integer salary;
    private Double gross;
    private Double benefitsCost;
    private Double net;

    public EmployeeResponse() {}

    public String getPartitionKey() { return partitionKey; }
    public void setPartitionKey(String partitionKey) { this.partitionKey = partitionKey; }

    public String getSortKey() { return sortKey; }
    public void setSortKey(String sortKey) { this.sortKey = sortKey; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Integer getDependants() { return dependants; }
    public void setDependants(Integer dependants) { this.dependants = dependants; }

    public String getExpiration() { return expiration; }
    public void setExpiration(String expiration) { this.expiration = expiration; }

    public Integer getSalary() { return salary; }
    public void setSalary(Integer salary) { this.salary = salary; }

    public Double getGross() { return gross; }
    public void setGross(Double gross) { this.gross = gross; }

    public Double getBenefitsCost() { return benefitsCost; }
    public void setBenefitsCost(Double benefitsCost) { this.benefitsCost = benefitsCost; }

    public Double getNet() { return net; }
    public void setNet(Double net) { this.net = net; }
}
