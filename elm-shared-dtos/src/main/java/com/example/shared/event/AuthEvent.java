package com.example.shared.event;

import com.example.shared.dto.AuthUserDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthEvent {
    private EventType eventType;
    private AuthUserDTO user;
}
