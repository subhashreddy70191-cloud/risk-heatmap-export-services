package com.riskheatmap.service;

import com.riskheatmap.entity.RiskItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendDailySummary(String to, long activeCount, long highRiskCount) {
        log.info("Sending daily summary email to {}", to);
        try {
            Context context = new Context();
            context.setVariable("activeCount", activeCount);
            context.setVariable("highRiskCount", highRiskCount);

            String htmlContent = templateEngine.process("daily-summary", context);

            sendHtmlEmail(to, "Daily Risk Heatmap Summary", htmlContent);
        } catch (Exception e) {
            log.error("Failed to send daily summary email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendDeadlineAlert(String to, String ownerName, List<RiskItem> dueItems) {
        log.info("Sending deadline alert email to {} for {} items", to, dueItems.size());
        try {
            Context context = new Context();
            context.setVariable("ownerName", ownerName);
            context.setVariable("dueItems", dueItems);

            String htmlContent = templateEngine.process("deadline-alert", context);

            sendHtmlEmail(to, "Action Required: Upcoming Risk Deadlines", htmlContent);
        } catch (Exception e) {
            log.error("Failed to send deadline alert email to {}: {}", to, e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Sender email (fromEmail) is not configured. Falling back to recipient address as sender.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail != null && !fromEmail.isBlank() ? fromEmail : to);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        try {
            mailSender.send(message);
            log.debug("Successfully sent HTML email to {} with subject: {}", to, subject);
        } catch (Exception e) {
            log.error("SMTP Error: Failed to send email to {}. Reason: {}", to, e.getMessage());
            throw e;
        }
    }
}
