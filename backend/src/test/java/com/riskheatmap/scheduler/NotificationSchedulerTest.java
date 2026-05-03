package com.riskheatmap.scheduler;

import com.riskheatmap.entity.RiskItem;
import com.riskheatmap.entity.User;
import com.riskheatmap.repository.RiskItemRepository;
import com.riskheatmap.repository.UserRepository;
import com.riskheatmap.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class NotificationSchedulerTest {

    @Mock
    private RiskItemRepository riskItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @Test
    void sendDailyReminders_Success() {
        ReflectionTestUtils.setField(notificationScheduler, "adminEmail", "admin@test.com");
        
        when(riskItemRepository.countActive()).thenReturn(10L);
        when(riskItemRepository.countHighRisk()).thenReturn(5L);

        notificationScheduler.sendDailyReminders();

        verify(emailService, times(1)).sendDailySummary("admin@test.com", 10L, 5L);
    }

    @Test
    void sendDeadlineAlerts_Success() {
        RiskItem item = RiskItem.builder()
                .title("Urgent Task")
                .owner("john_doe")
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@test.com");

        when(riskItemRepository.findItemsDueSoon(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(item));
        when(userRepository.findByUsernameIn(any())).thenReturn(List.of(user));

        notificationScheduler.sendDeadlineAlerts();

        verify(emailService, times(1)).sendDeadlineAlert(eq("john@test.com"), eq("john_doe"), anyList());
    }

    @Test
    void sendDeadlineAlerts_UserNotFound() {
        RiskItem item = RiskItem.builder()
                .title("Urgent Task")
                .owner("unknown")
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        when(riskItemRepository.findItemsDueSoon(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(item));
        when(userRepository.findByUsernameIn(any())).thenReturn(List.of());

        notificationScheduler.sendDeadlineAlerts();

        verify(emailService, never()).sendDeadlineAlert(anyString(), anyString(), anyList());
    }

    @Test
    void sendDeadlineAlerts_NoOwner() {
        RiskItem item = RiskItem.builder()
                .title("Urgent Task")
                .owner(null)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        when(riskItemRepository.findItemsDueSoon(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(item));

        notificationScheduler.sendDeadlineAlerts();

        verify(emailService, never()).sendDeadlineAlert(anyString(), anyString(), anyList());
    }
}
