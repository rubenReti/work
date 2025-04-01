package com.example.notification.service;

import com.example.shared.dto.EmployeeDTO;
import com.example.shared.dto.AuthUserDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class NotificationService {
	
	//will be set by constructir by @RequiredArgsConstructor
	private final JavaMailSender mailSender;

	
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	


    public void sendWelcomeEmail(EmployeeDTO employee) {
        log.info("📧 [WELCOME EMAIL]");
        log.info("To: " + employee.getEmail());
        log.info("Hi " + employee.getFirstName() + ", welcome to the team!");
        log.info("Your department: " + employee.getDepartment());
        
        
        String subject = "🎉 Welcome to the Team!";
        String content = String.format("Hi %s,\n\nWelcome to the company!\nDepartment: %s", employee.getFirstName(), employee.getDepartment());
        sendEmail(employee.getEmail(), subject, content);
             
    }

    public void sendCredentialsEmail(AuthUserDTO authUser) {
        log.info("📧 [CREDENTIALS EMAIL]");
        log.info("To: " + authUser.getEmail());
        log.info("Your login username: " + authUser.getUsername());
        log.info("Temporary password: " + authUser.getPassword());

        String subject = "🔐 Your Login Credentials";
        String content = String.format("Hello,\n\nHere are your login details:\nUsername: %s\nPassword: %s\nRole: %s",
                authUser.getUsername(), authUser.getPassword(), authUser.getRole());
        sendEmail(authUser.getEmail(), subject, content);
    	
      
    }
    
    
    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();  // represents a MIME style email message
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");  //Helper class for populating 

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.setFrom("noreply@elm-notify.local");

            mailSender.send(message);
            System.out.println("✅ Email sent to " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
