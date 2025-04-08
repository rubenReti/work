package com.example.shared.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceDTO {
    private String employeeEmail;
    private long totalAllowed;
    private long used;
    private long remaining;
}