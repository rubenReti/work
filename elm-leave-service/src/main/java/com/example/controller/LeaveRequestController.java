 package com.example.controller;

import com.example.service.LeaveRequestService;
import com.example.shared.dto.LeaveRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @GetMapping("/test-open")
    public ResponseEntity<String> testOpen() {
        return ResponseEntity.ok("Leave Service is ALIVE");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/request")
    public ResponseEntity<LeaveRequestDTO> requestLeave(@RequestBody LeaveRequestDTO dto, Authentication auth) {
        dto.setEmployeeEmail(auth.getName()); // inject from token
        return ResponseEntity.ok(leaveRequestService.requestLeave(dto));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestDTO> approve(@PathVariable Long id, Authentication auth) {
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

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public ResponseEntity<List<LeaveRequestDTO>> getMine(Authentication auth) {
        return ResponseEntity.ok(leaveRequestService.getByEmail(auth.getName()));
    }
}
