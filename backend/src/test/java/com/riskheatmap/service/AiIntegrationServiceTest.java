package com.riskheatmap.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskheatmap.entity.RiskItem;
import com.riskheatmap.repository.RiskItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"null", "unchecked"})
public class AiIntegrationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RiskItemRepository riskItemRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AiIntegrationService aiIntegrationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiIntegrationService, "aiServiceBaseUrl", "http://localhost:5000");
    }

    @Test
    void enrichRiskItemWithAi_Success() throws Exception {
        RiskItem item = RiskItem.builder().id(1L).isDeleted(false).build();
        when(riskItemRepository.findById(1L)).thenReturn(Optional.of(item));

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("summary", "Test summary");
        mockResponse.put("overview", "Test overview");
        mockResponse.put("recommendations", "Test recs");

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(mockResponse);
        when(objectMapper.writeValueAsString(any())).thenReturn("mappedJson");

        aiIntegrationService.enrichRiskItemWithAi(1L, "Input data");

        verify(riskItemRepository, times(1)).save(item);
    }

    @Test
    void enrichRiskItemWithAi_ItemNotFound() {
        when(riskItemRepository.findById(99L)).thenReturn(Optional.empty());

        aiIntegrationService.enrichRiskItemWithAi(99L, "Input data");

        verify(restTemplate, never()).postForObject(anyString(), any(), any(Class.class));
        verify(riskItemRepository, never()).save(any());
    }

    @Test
    void enrichRiskItemWithAi_NullResponse() {
        RiskItem item = RiskItem.builder().id(1L).isDeleted(false).build();
        when(riskItemRepository.findById(1L)).thenReturn(Optional.of(item));

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(null);

        aiIntegrationService.enrichRiskItemWithAi(1L, "Input data");

        verify(riskItemRepository, never()).save(any());
    }
}
