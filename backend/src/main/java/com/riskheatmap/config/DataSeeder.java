package com.riskheatmap.config;

import com.riskheatmap.entity.RiskItem;
import com.riskheatmap.repository.RiskItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RiskItemRepository riskItemRepository;

    @Override
    public void run(String... args) {
        if (riskItemRepository.count() == 0) {
            log.info("Seeding 30 RiskItems...");
            List<RiskItem> items = new ArrayList<>();
            String[] categories = {"OPERATIONAL", "FINANCIAL", "STRATEGIC", "COMPLIANCE", "TECHNICAL", "REPUTATIONAL"};
            String[] statuses = {"OPEN", "IN_PROGRESS", "MITIGATED", "CLOSED"};
            String[] owners = {"Alice", "Bob", "Charlie", "David", "Eve"};
            
            String[] riskTitles = {
                "Cloud Service Provider Outage", "SQL Injection Vulnerability in Auth", 
                "Incomplete Disaster Recovery Plan", "Third-party Library Zero-day", 
                "Unauthorized Data Access Attempt", "Physical Security Breach at Data Center",
                "API Rate Limiting Issues", "Memory Leak in Export Service",
                "Compliance Audit Failure", "Insufficient Backup Redundancy"
            };

            for (int i = 0; i < 30; i++) {
                int likelihood = (i % 5) + 1;
                int impact = ((i * 2) % 5) + 1;
                
                // Ensure some critical risks
                if (i < 3) { impact = 5; likelihood = 5; }

                RiskItem item = RiskItem.builder()
                        .title(riskTitles[i % riskTitles.length] + " #" + (i + 1))
                        .description("Automated assessment identifies this as a " + 
                                     (impact * likelihood > 15 ? "CRITICAL" : "significant") + 
                                     " risk requiring immediate " + (impact > 3 ? "executive" : "operational") + " review.")
                        .category(categories[i % categories.length])
                        .status(statuses[i % statuses.length])
                        .likelihoodScore(likelihood)
                        .impactScore(impact)
                        .owner(owners[i % owners.length])
                        .dueDate(LocalDateTime.now().plusDays(i % 14 + 1))
                        .createdBy("system")
                        .build();
                items.add(item);
            }
            
            riskItemRepository.saveAll(items);
            log.info("Seeded 30 RiskItems successfully.");
        } else {
            log.info("RiskItems table is not empty. Skipping seeding.");
        }
    }
}
