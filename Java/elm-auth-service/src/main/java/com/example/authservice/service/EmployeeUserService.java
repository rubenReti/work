// ============================ EmployeeUserService.java (Fixed) ============================
package com.example.authservice.service;

import com.example.authservice.entity.User;  // Our entity User
import com.example.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;
import java.util.stream.Collectors;

@Service
public class EmployeeUserService implements UserDetailsService {

    private final UserRepository userRepository;

    public EmployeeUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail()); // ✅ Use fully qualified name
        builder.password(user.getPassword());
        builder.roles(user.getRoles().stream().map(role -> role.getRoleName()).toArray(String[]::new));

        return builder.build();
    }
}
