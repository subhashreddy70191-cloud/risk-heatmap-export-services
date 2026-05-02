package com.riskheatmap.service;

import com.riskheatmap.entity.RiskItem;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "no-reply@test.com");
    }

    @Test
    void sendDailySummary_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");

        emailService.sendDailySummary("test@test.com", 10, 5);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendDeadlineAlert_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");

        RiskItem item = RiskItem.builder().title("Task 1").build();
        emailService.sendDeadlineAlert("test@test.com", "John", List.of(item));

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendDailySummary_Exception() {
        when(templateEngine.process(anyString(), any(Context.class))).thenThrow(new RuntimeException("Template error"));

        emailService.sendDailySummary("test@test.com", 10, 5);

        // Exception caught internally and logged, so mailSender.send() should never be called
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
