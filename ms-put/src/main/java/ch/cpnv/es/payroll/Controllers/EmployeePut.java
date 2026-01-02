package ch.cpnv.es.payroll.Controllers;

import ch.cpnv.es.payroll.Entities.Employee;
import ch.cpnv.es.payroll.Repositories.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeePut {

    private final EmployeeRepository repository;

    EmployeePut(EmployeeRepository repository) {
        this.repository = repository;
    }

    /* curl sample :
    curl -i -X PUT localhost:8080/api/v1/employees/2 ^
        -H "Content-type:application/json" ^
        -d "{\"name\": \"Samwise Bing\", \"role\": \"peer-to-peer\"}"
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> upsertEmployee(
            @PathVariable Long id,
            @RequestBody Employee employee
    ) {
        boolean exists = repository.existsById(id);

        employee.setId(id);
        Employee saved = repository.save(employee);

        if (exists) {
            // Updated
            return ResponseEntity.ok(saved);
        } else {
            // Created
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }
    }
}