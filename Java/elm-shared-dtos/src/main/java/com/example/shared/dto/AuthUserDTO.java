package com.example.shared.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
	
	
    private String username;
    private String email;  // ✅ Add email
    private List<String> roles;  // ✅ Change from String to List<String>
    private String password; // ✅ Add password field

    
    
}
