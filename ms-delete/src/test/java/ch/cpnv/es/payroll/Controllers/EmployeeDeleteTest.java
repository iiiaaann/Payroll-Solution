package ch.cpnv.es.payroll.Controllers;

import ch.cpnv.es.payroll.Entities.Employee;
import ch.cpnv.es.payroll.PayrollApplication;
import ch.cpnv.es.payroll.Repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = PayrollApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")

class EmployeeDeleteTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee existingEmployee;

    @BeforeEach
    void given_an_existing_employee() {

        // GIVEN
        employeeRepository.deleteAll();

        Employee employee = new Employee("Doe", "Supervisor");

        existingEmployee = employeeRepository.save(employee);
    }

    @Test
    void when_deleting_existing_employee_then_success() {

        // GIVEN
        // none

        // WHEN
        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/api/v1/employees/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        existingEmployee.getId()
                );

        // THEN (HTTP)
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);

        // THEN (database)
        assertThat(employeeRepository.findById(existingEmployee.getId()))
                .isEmpty();
    }

    @Test
    void when_deleting_nonexistent_employee_then_success() {
        // GIVEN
        Long nonExistentEmployeeId = 999L;

        // WHEN
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            "/api/v1/employees/{id}",
                            HttpMethod.DELETE,
                            null,
                            String.class,
                            nonExistentEmployeeId
                    );

            // THEN (HTTP)
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);

            assertThat(response.getBody())
                    .contains("Could not find employee " + nonExistentEmployeeId);
        }
}
