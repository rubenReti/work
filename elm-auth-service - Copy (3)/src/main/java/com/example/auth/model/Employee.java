package com.example.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String password;
 


    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(nullable = false)  // Add this to match the table
    private String username;  // Make sure this field exists
    
    @Column(unique = true, nullable = false)
    private String email;

}
