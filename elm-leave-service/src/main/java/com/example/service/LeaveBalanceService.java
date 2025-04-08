package com.example.service;

import com.example.entity.LeaveBalance;
import com.example.kafka.LeaveBalanceEventProducer;
import com.example.repository.LeaveBalanceRepository;
import com.example.shared.dto.EmployeeDTO;
import com.example.shared.dto.LeaveBalanceDTO;
import com.example.shared.event.EventType;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    @Autowired 
  private LeaveBalanceEventProducer leaveBalanceEventProducer;
    
	private static final Logger log = LoggerFactory.getLogger(LeaveBalanceService.class);



    
    // called by EmployeeEventListener handleEmployeeEvent() for EMPLOYEE_CREATED
    public void initializeBalanceForNewEmployee(EmployeeDTO dto) {
    	log.info("📧 RRR Inside initializeBalanceForNewEmployee");
        String email = dto.getEmail();
        boolean exists = leaveBalanceRepository.findByEmployeeEmail(email).isPresent();

        if (!exists) {
            LeaveBalance balance = LeaveBalance.builder()
                    .employeeEmail(email)
                    .totalAllowed(20)
                    .used(0)
                    .remaining(20)
                    .build();

            leaveBalanceRepository.save(balance);
            System.out.println("📘 Initialized leave balance for " + email);
            leaveBalanceEventProducer.send(EventType.LEAVE_BALANCE_INITIALIZED, email);

        }
    }

    public boolean deductDays(String email, long daysRequested) {
        Optional<LeaveBalance> optional = leaveBalanceRepository.findByEmployeeEmail(email);

        if (optional.isEmpty()) return false;

        LeaveBalance balance = optional.get();

        if (balance.getRemaining() < daysRequested) {
            return false; // not enough leave
        }

        balance.setUsed(balance.getUsed() + daysRequested);
        balance.setRemaining(balance.getRemaining() - daysRequested);

        leaveBalanceRepository.save(balance);
        return true;
    }
    
    
    public LeaveBalanceDTO updateAllowed(String email, int newTotal) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeEmail(email)
            .orElseThrow(() -> new RuntimeException("No balance found for: " + email));

        balance.setTotalAllowed(newTotal);
        balance.setRemaining(newTotal - balance.getUsed());

        leaveBalanceRepository.save(balance);

        leaveBalanceEventProducer.send(EventType.LEAVE_BALANCE_UPDATED, email);

        return toDTO(balance);
    }
    
    private LeaveBalanceDTO toDTO(LeaveBalance balance) {
        return LeaveBalanceDTO.builder()
                .employeeEmail(balance.getEmployeeEmail())
                .totalAllowed(balance.getTotalAllowed())
                .used(balance.getUsed())
                .remaining(balance.getRemaining())
                .build();
    }


}
