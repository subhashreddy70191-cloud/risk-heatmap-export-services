package com.riskheatmap.service;

import com.riskheatmap.dto.RiskItemRequest;
import com.riskheatmap.dto.RiskItemResponse;
import com.riskheatmap.entity.RiskItem;
import com.riskheatmap.exception.ResourceNotFoundException;
import com.riskheatmap.exception.ValidationException;
import com.riskheatmap.repository.RiskItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class RiskItemServiceImpl implements RiskItemService {

    private final RiskItemRepository riskItemRepository;
    private final AiIntegrationService aiIntegrationService;

    @Override
    @Transactional
    @CacheEvict(value = {"riskItem", "riskItemsPage"}, allEntries = true)
    public RiskItemResponse createRiskItem(RiskItemRequest request, String username) {
        log.info("Creating new risk item: {}", request.getTitle());
        validateRequest(request);

        RiskItem riskItem = RiskItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(request.getStatus() != null ? request.getStatus() : "OPEN")
                .likelihoodScore(request.getLikelihoodScore())
                .impactScore(request.getImpactScore())
                .owner(request.getOwner())
                .dueDate(request.getDueDate())
                .createdBy(username)
                .build();

        RiskItem savedItem = riskItemRepository.save(riskItem);
        
        // Trigger async AI Enrichment
        String inputForAi = request.getTitle() + ". " + request.getDescription();
        aiIntegrationService.enrichRiskItemWithAi(savedItem.getId(), inputForAi);

        return mapToResponse(savedItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"riskItem", "riskItemsPage"}, allEntries = true)
    public RiskItemResponse updateRiskItem(Long id, RiskItemRequest request) {
        log.info("Updating risk item with ID: {}", id);
        validateRequest(request);

        RiskItem existingItem = riskItemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskItem not found with id: " + id));

        existingItem.setTitle(request.getTitle());
        existingItem.setDescription(request.getDescription());
        existingItem.setCategory(request.getCategory());
        existingItem.setStatus(request.getStatus());
        existingItem.setLikelihoodScore(request.getLikelihoodScore());
        existingItem.setImpactScore(request.getImpactScore());
        existingItem.setOwner(request.getOwner());
        existingItem.setDueDate(request.getDueDate());

        RiskItem updatedItem = riskItemRepository.save(existingItem);
        return mapToResponse(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "riskItem", key = "#id")
    public RiskItemResponse getRiskItemById(Long id) {
        RiskItem item = riskItemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskItem not found with id: " + id));
        return mapToResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "riskItemsPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<RiskItemResponse> getAllActiveRiskItems(Pageable pageable) {
        return riskItemRepository.findByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"riskItem", "riskItemsPage"}, allEntries = true)
    public void deleteRiskItem(Long id) {
        log.info("Soft deleting risk item with ID: {}", id);
        RiskItem existingItem = riskItemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskItem not found with id: " + id));
        
        existingItem.setIsDeleted(true);
        riskItemRepository.save(existingItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RiskItemResponse> searchRiskItems(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveRiskItems(pageable);
        }
        return riskItemRepository.searchByKeyword(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RiskItemResponse> filterByCategory(String category, Pageable pageable) {
        return riskItemRepository.findByCategoryAndIsDeletedFalse(category, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RiskItemResponse> filterByStatus(String status, Pageable pageable) {
        return riskItemRepository.findByStatusAndIsDeletedFalse(status, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RiskItemResponse> filterByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new ValidationException("Invalid date range provided");
        }
        return riskItemRepository.findByDateRange(start, end, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskItemResponse> getItemsDueSoon(int daysThreshold) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusDays(daysThreshold);
        return riskItemRepository.findItemsDueSoon(now, threshold).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskItemResponse> getAllForExport() {
        return riskItemRepository.findAllActiveForExport().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateRequest(RiskItemRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new ValidationException("Category is required");
        }
        if (request.getLikelihoodScore() == null || request.getLikelihoodScore() < 1 || request.getLikelihoodScore() > 5) {
            throw new ValidationException("Likelihood score must be between 1 and 5");
        }
        if (request.getImpactScore() == null || request.getImpactScore() < 1 || request.getImpactScore() > 5) {
            throw new ValidationException("Impact score must be between 1 and 5");
        }
    }

    private RiskItemResponse mapToResponse(RiskItem item) {
        return RiskItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .category(item.getCategory())
                .status(item.getStatus())
                .likelihoodScore(item.getLikelihoodScore())
                .impactScore(item.getImpactScore())
                .riskScore(item.getRiskScore())
                .owner(item.getOwner())
                .dueDate(item.getDueDate())
                .aiDescription(item.getAiDescription())
                .aiRecommendations(item.getAiRecommendations())
                .aiReport(item.getAiReport())
                .createdBy(item.getCreatedBy())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
