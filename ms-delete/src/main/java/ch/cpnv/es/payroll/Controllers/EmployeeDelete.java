package ch.cpnv.es.payroll.Controllers;

import ch.cpnv.es.payroll.Repositories.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeDelete {

    private final EmployeeRepository repository;

    EmployeeDelete(EmployeeRepository repository){
        this.repository = repository;
    }

    /* curl sample :
    curl -i -X DELETE localhost:8080/api/v1/employees/2
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
