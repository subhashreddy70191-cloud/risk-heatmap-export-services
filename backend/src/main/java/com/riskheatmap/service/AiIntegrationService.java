package com.riskheatmap.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskheatmap.entity.RiskItem;
import com.riskheatmap.repository.RiskItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiIntegrationService {

    private final RestTemplate restTemplate;
    private final RiskItemRepository riskItemRepository;
    private final ObjectMapper objectMapper;

    @Value("${ai-service.base-url}")
    private String aiServiceBaseUrl;

    @Async
    @SuppressWarnings("unchecked")
    public void enrichRiskItemWithAi(@lombok.NonNull Long riskItemId, String input) {
        log.info("Starting async AI enrichment for RiskItem ID: {}", riskItemId);
        
        try {
            // Fetch the risk item first to ensure it exists
            java.util.Optional<RiskItem> riskItemOpt = riskItemRepository.findById(riskItemId);
            if (riskItemOpt.isEmpty() || riskItemOpt.get().getIsDeleted()) {
                log.warn("RiskItem ID: {} not found or deleted, aborting AI enrichment.", riskItemId);
                return;
            }
            RiskItem riskItem = riskItemOpt.get();

            // Prepare the request
            String url = aiServiceBaseUrl + "/generate-report";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("input", input);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Make the call
            log.debug("Calling Python AI service at {}", url);
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            if (response != null) {
                log.info("Successfully received AI response for RiskItem ID: {}", riskItemId);
                
                // Parse AI Description from overview/summary
                String summary = (String) response.getOrDefault("summary", "");
                String overview = (String) response.getOrDefault("overview", "");
                riskItem.setAiDescription(summary + "\n\n" + overview);

                // Parse Recommendations
                Object recommendationsObj = response.get("recommendations");
                if (recommendationsObj != null) {
                    riskItem.setAiRecommendations(objectMapper.writeValueAsString(recommendationsObj));
                }

                // Parse the full report
                riskItem.setAiReport(objectMapper.writeValueAsString(response));

                // Save back to DB
                riskItemRepository.save(riskItem);
                log.info("RiskItem ID: {} successfully enriched with AI data.", riskItemId);
            } else {
                log.warn("AI service returned null response for RiskItem ID: {}", riskItemId);
            }

        } catch (JsonProcessingException e) {
            log.error("Error serializing AI response for RiskItem ID: {}", riskItemId, e);
        } catch (Exception e) {
            log.error("Failed to fetch AI enrichment for RiskItem ID: {}", riskItemId, e);
        }
    }
}
