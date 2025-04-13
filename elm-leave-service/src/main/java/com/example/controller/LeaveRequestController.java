 package com.example.controller;

import com.example.service.LeaveRequestService;
import com.example.shared.dto.LeaveRequestDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/leaves")
public class LeaveRequestController {

	
	private static final Logger log = LoggerFactory.getLogger(LeaveRequestController.class);

    @Autowired
    private LeaveRequestService leaveRequestService;

    @GetMapping("/test-open")
    public ResponseEntity<String> testOpen() {
        return ResponseEntity.ok("Leave Service is ALIVE");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER', 'HR')")
    @PostMapping("/request")
    public ResponseEntity<LeaveRequestDTO> requestLeave(@RequestBody LeaveRequestDTO dto, Authentication auth) {
    	log.info("RRR LeaveRequest from authenticated user: {}", auth != null ? auth.getName() : "auth is null");

        dto.setEmployeeEmail(auth.getName()); // inject from token
        log.info("LeaveRequestDTO: {}", dto);

        return ResponseEntity.ok(leaveRequestService.requestLeave(dto));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestDTO> approve(@PathVariable Long id, Authentication auth) {
    	log.info("RRR approve from authenticated user: {}", auth != null ? auth.getName() : "auth is null");

    	
    	
        return ResponseEntity.ok(leaveRequestService.approve(id, auth.getName()));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestDTO> reject(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(leaveRequestService.reject(id, auth.getName()));
    }


    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    @GetMapping
    public ResponseEntity<List<LeaveRequestDTO>> getAll() {
        return ResponseEntity.ok(leaveRequestService.getAll());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER', 'HR')")
    @GetMapping("/my")
    public ResponseEntity<List<LeaveRequestDTO>> getMine(Authentication auth) {
        return ResponseEntity.ok(leaveRequestService.getByEmail(auth.getName()));
    }
}
