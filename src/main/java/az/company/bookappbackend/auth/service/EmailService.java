package az.company.bookappbackend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String otp, LocalDateTime expiration) {
        String subject = "Verify your email address";
        String body = String.format("Your verification code: %s%nExpires at: %s%nIf you didn't request, ignore.",
                otp, expiration);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String username) {
        String subject = "Welcome to BookApp";
        String body = String.format("Hi %s, welcome!", username);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}
