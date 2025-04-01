package com.example.auth.kafka;

import com.example.auth.model.Role;
import com.example.auth.model.Employee;
import java.util.Optional;
import com.example.auth.service.AuthService;
import com.example.shared.dto.AuthUserDTO;
import com.example.shared.event.EmployeeEvent;
import com.example.shared.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventListener {

    @Autowired
    private AuthService authService;

    @KafkaListener(topics = "employee-events", groupId = "auth-service-group")
    public void handleEmployeeEvents(EmployeeEvent event) {
        System.out.println("Received Kafka Event: " + event);

        switch (event.getEventType()) {
            case EMPLOYEE_CREATED -> authService.registerFromEmployeeDTO(event.getEmployee());
            case EMPLOYEE_UPDATED -> authService.updateAuthUserFromEmployeeDTO(event.getEmployee());
//            case EMPLOYEE_DELETED -> authService.disableUserByEmail(event.getEmployee().getEmail());
            case EMPLOYEE_DELETED -> authService.deleteUserByEmail(event.getEmployee().getEmail());

            default -> System.out.println("⚠️ Unsupported event type: " + event.getEventType());
        }
    }

   

//    private String mapDepartmentToRole(String department) {
//        return switch (department.toLowerCase()) {
//            case "hr" -> "HR";
//            case "it" -> "ADMIN";
//            case "management" -> "MANAGER";
//            default -> "USER";
//        };
//    }
}






//package com.example.auth.kafka;
//
//import com.example.auth.model.Role;
//import com.example.auth.model.Employee;
//import java.util.Optional;
//
//import com.example.auth.service.AuthService;
//import com.example.shared.dto.AuthUserDTO;
//import com.example.shared.event.EmployeeEvent;
//import com.example.shared.event.EventType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EmployeeEventListener {
//
//    @Autowired
//    private AuthService authService;
//
//    @KafkaListener(topics = "employee-events", groupId = "auth-service-group")
//    public void handleEmployeeCreatedEvent(EmployeeEvent event) {
//        System.out.println("📥 Received Kafka Event: " + event);
//
//        if (event.getEventType() == EventType.EMPLOYEE_CREATED) {
//            String username = (event.getEmployee().getFirstName() + "." + event.getEmployee().getLastName()).toLowerCase();
//            String role = inferRoleFromDepartment(event.getEmployee().getDepartment());
//
//            AuthUserDTO dto = new AuthUserDTO(
//                null,
//                username,
//                event.getEmployee().getEmail(),
//                "defaultPassword123", // TODO: auto-gen or set later
//                role
//            );
//
//            System.out.println("🔁 Mapping Employee → AuthUserDTO: " + dto);
//            authService.register(dto);
//        }
//    }
//
//    
//    //FIX THAT !!!!
//    private String inferRoleFromDepartment(String department) {
//        return switch (department.toLowerCase()) {
//            case "hr" -> "HR";
//            case "it" -> "ADMIN";
//            case "management" -> "MANAGER";
//            default -> "USER";
//        };
//    }
//    
//    
//    
//    
//    private void updateAuthUserFromEmployee(EmployeeEvent event) {
//        var dto = event.getEmployee();
//        String role = inferRoleFromDepartment(dto.getDepartment());
//        String username = (dto.getFirstName() + "." + dto.getLastName()).toLowerCase();
//
//        System.out.println("🔁 Updating AuthUser: " + dto.getEmail());
//
//        // Find user in Auth DB by email
//        Optional<Employee> optional = employeeRepository.findByEmail(dto.getEmail());
//        if (optional.isEmpty()) {
//            System.out.println("❌ Auth record not found for: " + dto.getEmail());
//            return;
//        }
//
//        Employee existing = optional.get();
//        existing.setUsername(username);
//        existing.setEmail(dto.getEmail());
//        existing.setRole(Role.valueOf(role)); // enum mapping
//
//        employeeRepository.save(existing);
//        System.out.println("✅ AuthUser updated.");
//    }
//
//}
