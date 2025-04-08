package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeEmail;

    @Column(nullable = false)
    private long totalAllowed;

    @Column(nullable = false)
    private long used;

    @Column(nullable = false)
    private long remaining;
}
