package com.example.authservice.service;
import com.example.authservice.entity.EmployeeUser;
import com.example.authservice.repository.EmployeeUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeUserRepository repository;

    public EmployeeUserDetailsService(EmployeeUserRepository repository) {
        this.repository = repository;
    }

    
    
    
    //Spring Security automatically delegates authentication to UserDetailsService because of its built-in behavior
    //since we defined a custom implementation of UserDetailsService, Spring calls it in athentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}