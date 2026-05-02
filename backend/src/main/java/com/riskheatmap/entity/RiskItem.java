package com.riskheatmap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "risk_items",
    indexes = {
        @Index(name = "idx_risk_status",     columnList = "status"),
        @Index(name = "idx_risk_category",   columnList = "category"),
        @Index(name = "idx_risk_score",      columnList = "risk_score"),
        @Index(name = "idx_risk_created_at", columnList = "created_at"),
        @Index(name = "idx_risk_created_by", columnList = "created_by")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Core Fields ───────────────────────────────────────────

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // OPERATIONAL|FINANCIAL|STRATEGIC|COMPLIANCE|TECHNICAL|REPUTATIONAL
    @Column(nullable = false, length = 50)
    private String category;

    // OPEN|IN_PROGRESS|MITIGATED|CLOSED
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "OPEN";

    // 1 (rare) to 5 (almost certain)
    @Column(name = "likelihood_score", nullable = false)
    private Integer likelihoodScore;

    // 1 (negligible) to 5 (catastrophic)
    @Column(name = "impact_score", nullable = false)
    private Integer impactScore;

    // Computed: likelihood × impact (1–25)
    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "owner", length = 100)
    private String owner;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    // ── AI Fields (filled async after create) ─────────────────

    @Column(name = "ai_description", columnDefinition = "TEXT")
    private String aiDescription;

    @Column(name = "ai_recommendations", columnDefinition = "TEXT")
    private String aiRecommendations;

    @Column(name = "ai_report", columnDefinition = "TEXT")
    private String aiReport;

    // ── Audit Fields ──────────────────────────────────────────

    // Soft delete — never physically remove rows
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Lifecycle Hooks ───────────────────────────────────────

    @PrePersist
    public void prePersist() {
        computeRiskScore();
        if (this.isDeleted == null) this.isDeleted = false;
        if (this.status == null)    this.status    = "OPEN";
    }

    @PreUpdate
    public void preUpdate() {
        computeRiskScore();
    }

    private void computeRiskScore() {
        if (likelihoodScore != null && impactScore != null) {
            this.riskScore = likelihoodScore * impactScore;
        }
    }
}