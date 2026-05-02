package com.riskheatmap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskItemResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private Integer likelihoodScore;
    private Integer impactScore;
    private Integer riskScore;
    private String owner;
    private LocalDateTime dueDate;
    private String aiDescription;
    private String aiRecommendations;
    private String aiReport;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
