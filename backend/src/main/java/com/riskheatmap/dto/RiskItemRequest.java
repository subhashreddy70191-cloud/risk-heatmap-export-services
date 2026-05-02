package com.riskheatmap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskItemRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private String status;

    @NotNull(message = "Likelihood score is required")
    @Min(value = 1, message = "Likelihood score must be between 1 and 5")
    @Max(value = 5, message = "Likelihood score must be between 1 and 5")
    private Integer likelihoodScore;

    @NotNull(message = "Impact score is required")
    @Min(value = 1, message = "Impact score must be between 1 and 5")
    @Max(value = 5, message = "Impact score must be between 1 and 5")
    private Integer impactScore;

    private String owner;

    private LocalDateTime dueDate;
}
