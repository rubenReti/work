package com.example.shared.event;

import com.example.shared.dto.LeaveRequestDTO;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveEvent {
    private EventType eventType;
    private LeaveRequestDTO leave;
    private Instant timestamp;
}
