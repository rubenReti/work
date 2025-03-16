// ============================ RoleRepository.java ============================
package com.example.authservice.repository;

import com.example.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
}
