package com.example.service;

import com.example.entity.Employee;
import com.example.kafka.EmployeeEventProducer;
import com.example.repository.EmployeeRepository;
import com.example.shared.dto.EmployeeDTO;
import com.example.shared.event.EmployeeEvent;
import com.example.shared.event.EventType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private EmployeeEventProducer eventProducer;

    // Fetch all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Fetch a single employee by ID
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // Add a new employee (Ensure department is set)
    public Employee addEmployee(Employee employee) {
        if (employee.getDepartment() == null || employee.getDepartment().isEmpty()) {
            employee.setDepartment("General"); // Assign default value if missing
        }
        Employee saved = employeeRepository.save(employee);

        EmployeeDTO dto = new EmployeeDTO(
            saved.getId(),
            saved.getFirstName(),
            saved.getLastName(),
            saved.getEmail(),
            saved.getDepartment()
        );

        EmployeeEvent event = EmployeeEvent.builder()
            .eventType(EventType.EMPLOYEE_CREATED)
            .employee(dto)
            .timestamp(java.time.Instant.now())
            .build();

        eventProducer.sendEvent(event);
        return saved;
    }

    // Update an existing employee (Ensure department is set)
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id).map(existing -> {
            existing.setFirstName(updatedEmployee.getFirstName());
            existing.setLastName(updatedEmployee.getLastName());
            existing.setEmail(updatedEmployee.getEmail());
            existing.setDepartment(updatedEmployee.getDepartment() != null ? updatedEmployee.getDepartment() : "General");

            Employee saved = employeeRepository.save(existing);

            // Send EMPLOYEE_UPDATED event
            EmployeeDTO dto = new EmployeeDTO(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getDepartment()
            );

            EmployeeEvent event = EmployeeEvent.builder()
                .eventType(EventType.EMPLOYEE_UPDATED)
                .employee(dto)
                .timestamp(java.time.Instant.now())
                .build();

            eventProducer.sendEvent(event); 

            return saved;
        }).orElse(null);
    }


    public void deleteEmployee(Long id) {
        employeeRepository.findById(id).ifPresent(employee -> {
            // Create DTO for event
            EmployeeDTO dto = new EmployeeDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment()
            );

            EmployeeEvent event = EmployeeEvent.builder()
                .eventType(EventType.EMPLOYEE_DELETED)
                .employee(dto)
                .timestamp(java.time.Instant.now())
                .build();

            eventProducer.sendEvent(event); // 🔁 Reuse same producer

            employeeRepository.deleteById(id);
            System.out.println("✅ Employee deleted and event sent: " + id);
        });
    }

}
