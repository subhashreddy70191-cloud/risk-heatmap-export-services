package com.riskheatmap.scheduler;

import com.riskheatmap.entity.RiskItem;
import com.riskheatmap.entity.User;
import com.riskheatmap.repository.RiskItemRepository;
import com.riskheatmap.repository.UserRepository;
import com.riskheatmap.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final RiskItemRepository riskItemRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.admin-email}")
    private String adminEmail;

    /**
     * Daily Reminder: Runs every day at 8:00 AM
     * Sends a summary of active and high-severity risks to the admin.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyReminders() {
        log.info("Starting daily risk summary scheduler...");
        
        long activeCount = riskItemRepository.countActive();
        long highRiskCount = riskItemRepository.countHighRisk();

        if (activeCount > 0) {
            emailService.sendDailySummary(adminEmail, activeCount, highRiskCount);
        } else {
            log.info("No active risks to report today.");
        }
    }

    /**
     * Deadline Alert: Runs every day at 9:00 AM
     * Sends alerts to users who have risks due within the next 7 days.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDeadlineAlerts() {
        log.info("Starting deadline alert scheduler...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusDays(7);

        List<RiskItem> dueSoonItems = riskItemRepository.findItemsDueSoon(now, threshold);

        if (dueSoonItems.isEmpty()) {
            log.info("No items due within the next 7 days.");
            return;
        }

        // Group items by owner username
        Map<String, List<RiskItem>> itemsByOwner = dueSoonItems.stream()
                .filter(item -> item.getOwner() != null && !item.getOwner().trim().isEmpty())
                .collect(Collectors.groupingBy(RiskItem::getOwner));

        if (itemsByOwner.isEmpty()) {
            log.info("No items with valid owners to alert.");
            return;
        }

        // Batch fetch all owners to avoid N+1 queries
        List<User> owners = userRepository.findByUsernameIn(itemsByOwner.keySet());
        Map<String, User> userMap = owners.stream()
                .collect(Collectors.toMap(User::getUsername, user -> user));

        itemsByOwner.forEach((ownerUsername, items) -> {
            User user = userMap.get(ownerUsername);
            
            if (user != null) {
                String ownerName = user.getFullName() != null ? user.getFullName() : user.getUsername();
                emailService.sendDeadlineAlert(user.getEmail(), ownerName, items);
            } else {
                log.warn("Owner '{}' not found in users table. Skipping deadline alert for {} items.", ownerUsername, items.size());
            }
        });

        log.info("Deadline alerts sent to {} unique owners.", owners.size());
    }
}
