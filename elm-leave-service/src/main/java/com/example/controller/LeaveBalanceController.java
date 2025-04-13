package com.example.controller;

import com.example.service.LeaveBalanceService;
import com.example.shared.dto.LeaveBalanceDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")   //for swagger ONLY
@RestController
@RequestMapping("/leave-balance")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @PutMapping("/{email}/allowed")
    public ResponseEntity<LeaveBalanceDTO> updateAllowed(@PathVariable String email, @RequestBody int newAllowed) {
        return ResponseEntity.ok(leaveBalanceService.updateAllowed(email, newAllowed));

    }
}
 