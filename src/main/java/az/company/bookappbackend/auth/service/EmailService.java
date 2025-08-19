package az.company.bookappbackend.auth.service;

import az.company.bookappbackend.auth.exception.EmailSendException;
import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(UserEntity user, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", user.getName());
            context.setVariable("otp", otp);
            context.setVariable("expiryMinutes", 15);

            String htmlContent = templateEngine.process("verification-email", context);

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Verify your email address");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
            throw new EmailSendException("Email send failed");
        }
    }

    @Async
    public void sendWelcomeEmail(UserEntity user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", user.getName());

            String htmlContent = templateEngine.process("welcome-email", context);

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Welcome to BookApp!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
            throw new EmailSendException("Welcome email send failed");
        }
    }
}
