package com.riskheatmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RiskHeatmapApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiskHeatmapApplication.class, args);
    }
}