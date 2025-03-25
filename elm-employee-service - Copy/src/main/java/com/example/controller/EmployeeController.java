// ===================== Updated EmployeeController.java =====================
package com.example.controller;

import com.example.entity.Employee;
import com.example.service.EmployeeService;
import com.example.shared.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    
    @GetMapping("/test-open")
    public ResponseEntity<String> testOpen() {
        System.out.println("👋 /test-open endpoint HIT!");
        return ResponseEntity.ok("Employee Service is ALIVE");
    }

    // Get all employees
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees().stream()
                .map(emp -> new EmployeeDTO(emp.getId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getDepartment()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    // Get an employee by ID
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(emp -> ResponseEntity.ok(new EmployeeDTO(
                emp.getId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getDepartment()
        ))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a new employee
    @PreAuthorize("hasAnyRole('HR')")
    @PostMapping
    public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeService.addEmployee(new Employee(
                null, employeeDTO.getFirstName(), employeeDTO.getLastName(), employeeDTO.getEmail(), employeeDTO.getDepartment(), null
        ));
        return ResponseEntity.ok(new EmployeeDTO(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getDepartment()));
    }

    // Update an existing employee
    @PreAuthorize("hasAnyRole('HR')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO updatedEmployee) {
        Employee employee = employeeService.updateEmployee(id, new Employee(
                id, updatedEmployee.getFirstName(), updatedEmployee.getLastName(), updatedEmployee.getEmail(), updatedEmployee.getDepartment(), null
        ));
        if (employee != null) {
            return ResponseEntity.ok(new EmployeeDTO(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getDepartment()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an employee by ID
    @PreAuthorize("hasAnyRole('HR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
