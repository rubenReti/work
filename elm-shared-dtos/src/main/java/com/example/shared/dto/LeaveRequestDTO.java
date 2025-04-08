package com.example.shared.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestDTO {
    private Long id;
    private String employeeEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;  // ENUM as string: REQUESTED, APPROVED, etc.
}
