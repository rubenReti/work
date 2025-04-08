package com.example.shared.event;
import com.example.shared.dto.LeaveRequestDTO;
import lombok.*;
import java.time.Instant;




@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceEvent {
    private EventType eventType; // LEAVE_BALANCE_INITIALIZED or LEAVE_BALANCE_UPDATED
    private String email;
    private Instant timestamp;
}


