
package com.example.shared.event;

import com.example.shared.dto.EmployeeDTO;
import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvent {
    private EventType eventType;
    private EmployeeDTO employee;
    private Instant timestamp;
}
