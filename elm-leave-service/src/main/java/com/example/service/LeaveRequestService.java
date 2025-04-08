package com.example.service;

import com.example.entity.LeaveBalance;
import com.example.entity.LeaveRequest;
import com.example.entity.LeaveRequest.LeaveStatus;
import com.example.kafka.LeaveEventProducer;
import com.example.repository.LeaveBalanceRepository;
import com.example.repository.LeaveRequestRepository;
import com.example.shared.dto.LeaveBalanceDTO;
import com.example.shared.dto.LeaveRequestDTO;
import com.example.shared.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //generates the constructor ->Spring auto-wires via constructor instead of @Autowired on fields
public class LeaveRequestService {

	
//	@Autowired 
	private RedisLockService lockService;

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveEventProducer eventProducer;
    private final LeaveBalanceService leaveBalanceService;

    public List<LeaveRequestDTO> getAll() {
        return leaveRequestRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<LeaveRequestDTO> getByEmail(String email) {
        return leaveRequestRepository.findByEmployeeEmail(email).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public LeaveRequestDTO requestLeave(LeaveRequestDTO dto) {
        LeaveRequest entity = LeaveRequest.builder()
                .employeeEmail(dto.getEmployeeEmail())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(LeaveStatus.REQUESTED)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(entity);
        LeaveRequestDTO result = toDTO(saved);

        eventProducer.sendLeaveEvent(EventType.LEAVE_REQUESTED, result);
        return result;
    }

    
    
    //loc across instances by redis 
    public LeaveRequestDTO approve(Long id, String managerEmail) {
    	
    	
    	   LeaveRequest request = leaveRequestRepository.findById(id).orElseThrow();
    	    String email = request.getEmployeeEmail();
    	    String lockKey = "lock:leave:" + email + ":" + id;

    	    String lockId = lockService.tryLock(lockKey);
    	    if (lockId == null) {
    	        throw new IllegalStateException("This leave request is already being processed");
    	    }
    	    

    	    try {  	    
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        boolean allowed = leaveBalanceService.deductDays(request.getEmployeeEmail(), days);
        if (!allowed) throw new RuntimeException("Not enough leave balance");

        request.setStatus(LeaveStatus.APPROVED);
        request.setActionedBy(managerEmail); // 🧠
        LeaveRequest updated = leaveRequestRepository.save(request);

        LeaveRequestDTO dto = toDTO(updated);
        eventProducer.sendLeaveEvent(EventType.LEAVE_APPROVED, dto);
        return dto;
    	    } finally {
    	        lockService.releaseLock(lockKey, lockId);
    	    }
    }
    
    
    public LeaveRequestDTO getById(Long id) {
        return leaveRequestRepository.findById(id)
            .map(this::toDTO)   //equals to :    .map(e -> this.toDTO(e))
            .orElseThrow(() -> new RuntimeException("Not found"));
    }




    public LeaveRequestDTO reject(Long id, String managerEmail) {
        LeaveRequest request = leaveRequestRepository.findById(id).orElseThrow();
        String email = request.getEmployeeEmail();
        String lockKey = "lock:leave:" + email + ":" + id;

        String lockId = lockService.tryLock(lockKey);
        if (lockId == null) {
            throw new IllegalStateException("This leave request is already being processed");
        }

        try {
            request.setStatus(LeaveStatus.REJECTED);
            request.setActionedBy(managerEmail);

            LeaveRequest updated = leaveRequestRepository.save(request);
            LeaveRequestDTO dto = toDTO(updated);

            eventProducer.sendLeaveEvent(EventType.LEAVE_REJECTED, dto);

            return dto;
        } finally {
            lockService.releaseLock(lockKey, lockId);
        }
    }



    
  

    private LeaveRequestDTO toDTO(LeaveRequest e) {
        return LeaveRequestDTO.builder()
                .id(e.getId())
                .employeeEmail(e.getEmployeeEmail())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .reason(e.getReason())
                .status(e.getStatus().name())
                .build();
    }
}
