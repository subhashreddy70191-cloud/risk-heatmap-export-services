package com.riskheatmap.service;

import com.riskheatmap.dto.RiskItemRequest;
import com.riskheatmap.dto.RiskItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface RiskItemService {

    RiskItemResponse createRiskItem(RiskItemRequest request, String username);

    RiskItemResponse updateRiskItem(Long id, RiskItemRequest request);

    RiskItemResponse getRiskItemById(Long id);

    Page<RiskItemResponse> getAllActiveRiskItems(Pageable pageable);

    void deleteRiskItem(Long id);

    Page<RiskItemResponse> searchRiskItems(String keyword, Pageable pageable);

    Page<RiskItemResponse> filterByCategory(String category, Pageable pageable);

    Page<RiskItemResponse> filterByStatus(String status, Pageable pageable);

    Page<RiskItemResponse> filterByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<RiskItemResponse> getItemsDueSoon(int daysThreshold);
    
    List<RiskItemResponse> getAllForExport();
}
