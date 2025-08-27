package az.company.bookappbackend.auth.service;

import az.company.bookappbackend.auth.exception.EmailSendException;
import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(UserEntity user, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            String body = getVerificationEmailBody(user, otp);

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Verify your email address");
            helper.setText(body, false);

            mailSender.send(message);
            log.info("EmailService::sendVerificationEmail Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("EmailService::sendVerificationEmailFailed to send verification email to: {}", user.getEmail(),
                    e);
            throw new EmailSendException("Email send failed");
        }
    }

    private String getVerificationEmailBody(UserEntity user, String otp) {
        String recipientName = user.getName() == null ? "User" : user.getName();
        return String.format("""
                        Hello %s,
                        
                        To verify your account, please use the following OTP code:
                        
                          %s
                        
                        This code is valid for %d minutes.
                        
                        If you did not request this, please ignore this email.
                        
                        Regards,
                        The BookApp Team""",
                recipientName, otp, 15);
    }

    @Async
    public void sendWelcomeEmail(UserEntity user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            String body = getWelcomeEmailBody(user);

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Welcome to BookApp!");
            helper.setText(body, false);

            mailSender.send(message);
            log.info("EmailService::sendWelcomeEmail Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("EmailService::sendWelcomeEmail Failed to send welcome email to: {}", user.getEmail(), e);
            throw new EmailSendException("Welcome email send failed");
        }
    }

    private String getWelcomeEmailBody(UserEntity user) {
        String recipientName = user.getName() == null ? "User" : user.getName();
        return String.format("""
                        Hello %s,
                        
                        Welcome to BookApp! Your account is now active.
                        
                        We hope you enjoy using our service.
                        
                        Best regards,
                        The BookApp Team""",
                recipientName);
    }
}
