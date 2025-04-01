package com.example.notification.service;

import com.example.shared.dto.EmployeeDTO;
import com.example.shared.dto.AuthUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationCorrelationStore {

    private final NotificationService notificationService;

    private record EventState(
        boolean employeeCreated,
        boolean authCreated,
        EmployeeDTO employee,
        AuthUserDTO authUser
    ) {}

    private final ConcurrentHashMap<String, EventState> store = new ConcurrentHashMap<>();

    public synchronized void handleEmployeeCreated(EmployeeDTO dto, Runnable postCheck) {
        String email = dto.getEmail();
        store.merge(email, new EventState(true, false, dto, null), (oldVal, newVal) -> {
            EventState updated = new EventState(true, oldVal.authCreated, dto, oldVal.authUser);
            if (updated.authCreated) trigger(email, updated);
            return updated;
        });
    }

    public synchronized void handleAuthCreated(AuthUserDTO dto, Runnable postCheck) {
        String email = dto.getEmail();
        store.merge(email, new EventState(false, true, null, dto), (oldVal, newVal) -> {
            EventState updated = new EventState(oldVal.employeeCreated, true, oldVal.employee, dto);
            if (updated.employeeCreated) trigger(email, updated);
            return updated;
        });
    }

    private void trigger(String email, EventState state) {
        System.out.println("🚨 Triggering notification for: " + email);
        notificationService.sendWelcomeEmail(state.employee);
        notificationService.sendCredentialsEmail(state.authUser);
        store.remove(email);
    }
}






// package com.example.notification.service;
//
//import com.example.shared.dto.EmployeeDTO;
//import com.example.shared.dto.AuthUserDTO;
//import lombok.*;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotificationCorrelationStore {
//    record EventState(boolean employeeCreated, boolean authCreated, EmployeeDTO employee, AuthUserDTO authUser) {}
//
//    private final ConcurrentHashMap<String, EventState> store = new ConcurrentHashMap<>();
//
//    
//    
////    Runnable is  a functional interface allows us to pass logic around like a method reference /lambda
//    public synchronized void handleEmployeeCreated(EmployeeDTO dto, Runnable onBothReady) {
//        String email = dto.getEmail();
//        
//        
////        merge - thread-safe operation on ConcurrentHashMap
////        If email is NOT in the map → insert value as new
////        If email already exists, use remappingFunction to combine old and new values
//        store.merge(email, 
//        		new EventState(true, false, dto, null),    	// value to insert if new
//        		(oldVal, newVal) -> {						// if already exists, merge logic
//            EventState updated = new EventState(true, oldVal.authCreated, dto, oldVal.authUser);
//            
////            If Auth part has already arrived, and now Employee just came in → we’re done!" 
////            → Run the callback onBothReady (which will trigger email sending logic, etc)
//            if (updated.authCreated) onBothReady.run();
//            return updated;
//        });
//    }
//
//    public synchronized void handleAuthCreated(AuthUserDTO dto, Runnable onBothReady) {
//        String email = dto.getEmail();
//        store.merge(email, new EventState(false, true, null, dto), (oldVal, newVal) -> {
//            EventState updated = new EventState(oldVal.employeeCreated, true, oldVal.employee, dto);
//            if (updated.employeeCreated) onBothReady.run();
//            return updated;
//        });
//    }
//
//    public EventState get(String email) {
//        return store.get(email);
//    }
//
//    public void cleanup(String email) {
//        store.remove(email);
//    }
//}
