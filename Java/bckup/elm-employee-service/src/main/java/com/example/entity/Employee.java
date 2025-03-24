package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter // Lombok generates all getter methods
@Setter // Lombok generates all setter methods
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String department = "General";  // Ensure default value in Java

    private LocalDateTime createdAt = LocalDateTime.now();

    // Ensure department is never null when setting
    public void setDepartment(String department) {
        this.department = (department != null && !department.isEmpty()) ? department : "General";
    }
}
