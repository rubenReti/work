package com.example.authservice.repository;

import com.example.authservice.entity.EmployeeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeUserRepository extends JpaRepository<EmployeeUser, Long> {
    Optional<EmployeeUser> findByUsername(String username);
}