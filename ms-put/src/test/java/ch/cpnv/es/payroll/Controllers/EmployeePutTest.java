package ch.cpnv.es.payroll.Controllers;

import ch.cpnv.es.payroll.Entities.Employee;
import ch.cpnv.es.payroll.PayrollApplication;
import ch.cpnv.es.payroll.Repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = PayrollApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class EmployeePutTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee existingEmployee;

    @BeforeEach
    void given_an_existing_employee() {
        employeeRepository.deleteAll();

        Employee employee = new Employee("Doe", "Supervisor");
        existingEmployee = employeeRepository.save(employee);
    }

    @Test
    void when_updating_existing_employee_then_success() {
        // GIVEN
        Employee updatedInfo = new Employee("Doe", "Manager"); // change role
        HttpEntity<Employee> request = new HttpEntity<>(updatedInfo);

        // WHEN
        ResponseEntity<Employee> response =
                restTemplate.exchange(
                        "/api/v1/employees/{id}",
                        HttpMethod.PUT,
                        request,
                        Employee.class,
                        existingEmployee.getId()
                );

        // THEN (HTTP)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // THEN (body)
        Employee updatedEmployee = response.getBody();
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getId()).isEqualTo(existingEmployee.getId());
        assertThat(updatedEmployee.getRole()).isNotNull();

        // THEN (database)
        Employee dbEmployee = employeeRepository.findById(existingEmployee.getId()).orElseThrow();
        assertThat(dbEmployee.getRole()).isEqualTo("MANAGER");
    }

    @Test
    void when_updating_nonexistent_employee_then_created() {
        // GIVEN
        Long nonExistentId = 999L;
        Employee newEmployee = new Employee("Smith", "Engineer");
        HttpEntity<Employee> request = new HttpEntity<>(newEmployee);

        // WHEN
        ResponseEntity<Employee> response =
                restTemplate.exchange(
                        "/api/v1/employees/{id}",
                        HttpMethod.PUT,
                        request,
                        Employee.class,
                        nonExistentId
                );

        // THEN (HTTP)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // THEN (body)
        Employee createdEmployee = response.getBody();
        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getId()).isNotNull();
        assertThat(createdEmployee.getName()).isEqualTo("Smith");
        assertThat(createdEmployee.getRole()).isEqualTo("ENGINEER");

        // THEN (database)
        Employee dbEmployee = employeeRepository.findById(createdEmployee.getId()).orElseThrow();
        assertThat(dbEmployee.getName()).isEqualTo("Smith");
    }
}