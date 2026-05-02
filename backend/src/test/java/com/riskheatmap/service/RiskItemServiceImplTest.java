package com.riskheatmap.service;

import com.riskheatmap.dto.RiskItemRequest;
import com.riskheatmap.dto.RiskItemResponse;
import com.riskheatmap.entity.RiskItem;
import com.riskheatmap.exception.ResourceNotFoundException;
import com.riskheatmap.exception.ValidationException;
import com.riskheatmap.repository.RiskItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class RiskItemServiceImplTest {

    @Mock
    private RiskItemRepository riskItemRepository;

    @Mock
    private AiIntegrationService aiIntegrationService;

    @InjectMocks
    private RiskItemServiceImpl riskItemService;

    @Test
    void createRiskItem_Success() {
        RiskItemRequest request = new RiskItemRequest();
        request.setTitle("Test Title");
        request.setCategory("TECHNICAL");
        request.setLikelihoodScore(3);
        request.setImpactScore(4);

        RiskItem savedItem = RiskItem.builder()
                .id(1L)
                .title("Test Title")
                .category("TECHNICAL")
                .likelihoodScore(3)
                .impactScore(4)
                .riskScore(12)
                .status("OPEN")
                .build();

        when(riskItemRepository.save(any(RiskItem.class))).thenReturn(savedItem);

        RiskItemResponse response = riskItemService.createRiskItem(request, "admin");

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        assertEquals(12, response.getRiskScore());
        verify(riskItemRepository, times(1)).save(any(RiskItem.class));
        verify(aiIntegrationService, times(1)).enrichRiskItemWithAi(eq(1L), anyString());
    }

    @Test
    void createRiskItem_ValidationFailure_MissingTitle() {
        RiskItemRequest request = new RiskItemRequest();
        request.setCategory("TECHNICAL");
        request.setLikelihoodScore(3);
        request.setImpactScore(4);

        assertThrows(ValidationException.class, () -> riskItemService.createRiskItem(request, "admin"));
        verify(riskItemRepository, never()).save(any());
    }

    @Test
    void getRiskItemById_Success() {
        RiskItem item = RiskItem.builder().id(1L).title("Existing Item").build();
        when(riskItemRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(item));

        RiskItemResponse response = riskItemService.getRiskItemById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Existing Item", response.getTitle());
    }

    @Test
    void getRiskItemById_NotFound() {
        when(riskItemRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> riskItemService.getRiskItemById(99L));
    }

    @Test
    void updateRiskItem_Success() {
        RiskItemRequest request = new RiskItemRequest();
        request.setTitle("Updated Title");
        request.setCategory("FINANCIAL");
        request.setLikelihoodScore(2);
        request.setImpactScore(2);

        RiskItem existingItem = RiskItem.builder().id(1L).title("Old Title").build();
        RiskItem updatedItem = RiskItem.builder().id(1L).title("Updated Title").riskScore(4).build();

        when(riskItemRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingItem));
        when(riskItemRepository.save(any(RiskItem.class))).thenReturn(updatedItem);

        RiskItemResponse response = riskItemService.updateRiskItem(1L, request);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        assertEquals(4, response.getRiskScore());
    }

    @Test
    void updateRiskItem_NotFound() {
        RiskItemRequest request = new RiskItemRequest();
        request.setTitle("Valid Title");
        request.setCategory("FINANCIAL");
        request.setLikelihoodScore(2);
        request.setImpactScore(2);

        when(riskItemRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> riskItemService.updateRiskItem(99L, request));
        verify(riskItemRepository, never()).save(any());
    }

    @Test
    void deleteRiskItem_Success() {
        RiskItem existingItem = RiskItem.builder().id(1L).isDeleted(false).build();
        when(riskItemRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingItem));
        when(riskItemRepository.save(any(RiskItem.class))).thenReturn(existingItem);

        riskItemService.deleteRiskItem(1L);

        assertTrue(existingItem.getIsDeleted());
        verify(riskItemRepository, times(1)).save(existingItem);
    }

    @Test
    void deleteRiskItem_NotFound() {
        when(riskItemRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> riskItemService.deleteRiskItem(99L));
        verify(riskItemRepository, never()).save(any());
    }

    @Test
    void getAllActiveRiskItems_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        RiskItem item1 = RiskItem.builder().id(1L).title("Item 1").build();
        RiskItem item2 = RiskItem.builder().id(2L).title("Item 2").build();
        Page<RiskItem> page = new PageImpl<>(List.of(item1, item2));

        when(riskItemRepository.findByIsDeletedFalse(pageable)).thenReturn(page);

        Page<RiskItemResponse> responsePage = riskItemService.getAllActiveRiskItems(pageable);

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals("Item 1", responsePage.getContent().get(0).getTitle());
    }

    @Test
    void searchRiskItems_WithKeyword_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        RiskItem item = RiskItem.builder().id(1L).title("Cyber Attack").build();
        Page<RiskItem> page = new PageImpl<>(List.of(item));

        when(riskItemRepository.searchByKeyword("Cyber", pageable)).thenReturn(page);

        Page<RiskItemResponse> responsePage = riskItemService.searchRiskItems("Cyber", pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Cyber Attack", responsePage.getContent().get(0).getTitle());
    }

    @Test
    void searchRiskItems_WithEmptyKeyword_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        RiskItem item = RiskItem.builder().id(1L).title("All Items").build();
        Page<RiskItem> page = new PageImpl<>(List.of(item));

        when(riskItemRepository.findByIsDeletedFalse(pageable)).thenReturn(page);

        Page<RiskItemResponse> responsePage = riskItemService.searchRiskItems("", pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    void filterByCategory_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        RiskItem item = RiskItem.builder().id(1L).category("TECHNICAL").build();
        Page<RiskItem> page = new PageImpl<>(List.of(item));

        when(riskItemRepository.findByCategoryAndIsDeletedFalse("TECHNICAL", pageable)).thenReturn(page);

        Page<RiskItemResponse> responsePage = riskItemService.filterByCategory("TECHNICAL", pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("TECHNICAL", responsePage.getContent().get(0).getCategory());
    }

    @Test
    void filterByStatus_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        RiskItem item = RiskItem.builder().id(1L).status("OPEN").build();
        Page<RiskItem> page = new PageImpl<>(List.of(item));

        when(riskItemRepository.findByStatusAndIsDeletedFalse("OPEN", pageable)).thenReturn(page);

        Page<RiskItemResponse> responsePage = riskItemService.filterByStatus("OPEN", pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("OPEN", responsePage.getContent().get(0).getStatus());
    }

    @Test
    void filterByDateRange_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        java.time.LocalDateTime start = java.time.LocalDateTime.now().minusDays(1);
        java.time.LocalDateTime end = java.time.LocalDateTime.now().plusDays(1);
        RiskItem item = RiskItem.builder().id(1L).build();
        Page<RiskItem> page = new PageImpl<>(List.of(item));

        when(riskItemRepository.findByDateRange(start, end, pageable)).thenReturn(page);

        Page<RiskItemResponse> responsePage = riskItemService.filterByDateRange(start, end, pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    void filterByDateRange_ValidationFailure() {
        Pageable pageable = PageRequest.of(0, 10);
        java.time.LocalDateTime start = java.time.LocalDateTime.now().plusDays(1);
        java.time.LocalDateTime end = java.time.LocalDateTime.now().minusDays(1);

        assertThrows(ValidationException.class, () -> riskItemService.filterByDateRange(start, end, pageable));
    }

    @Test
    void getItemsDueSoon_Success() {
        RiskItem item = RiskItem.builder().id(1L).build();
        when(riskItemRepository.findItemsDueSoon(any(), any())).thenReturn(List.of(item));

        List<RiskItemResponse> items = riskItemService.getItemsDueSoon(7);

        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    void getAllForExport_Success() {
        RiskItem item = RiskItem.builder().id(1L).build();
        when(riskItemRepository.findAllActiveForExport()).thenReturn(List.of(item));

        List<RiskItemResponse> items = riskItemService.getAllForExport();

        assertNotNull(items);
        assertEquals(1, items.size());
    }
}
