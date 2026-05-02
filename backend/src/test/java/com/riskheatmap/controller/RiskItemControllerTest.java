package com.riskheatmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskheatmap.dto.RiskItemRequest;
import com.riskheatmap.dto.RiskItemResponse;
import com.riskheatmap.service.RiskItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
public class RiskItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskItemService riskItemService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private com.riskheatmap.util.JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllRiskItems_Success() throws Exception {
        RiskItemResponse response = RiskItemResponse.builder().id(1L).title("Test").build();
        Page<RiskItemResponse> page = new PageImpl<>(List.of(response));

        when(riskItemService.getAllActiveRiskItems(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/risk-items/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test"));
    }

    @Test
    void getRiskItemById_Success() throws Exception {
        RiskItemResponse response = RiskItemResponse.builder().id(1L).title("Test").build();
        when(riskItemService.getRiskItemById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/risk-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createRiskItem_Success() throws Exception {
        RiskItemRequest request = new RiskItemRequest();
        request.setTitle("New Test");
        request.setCategory("TECHNICAL");
        request.setLikelihoodScore(3);
        request.setImpactScore(4);

        RiskItemResponse response = RiskItemResponse.builder().id(1L).title("New Test").build();

        // Without principal mock in simple test, we mock any string
        when(riskItemService.createRiskItem(any(RiskItemRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/risk-items/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateRiskItem_Success() throws Exception {
        RiskItemRequest request = new RiskItemRequest();
        request.setTitle("Updated Test");
        request.setCategory("TECHNICAL");
        request.setLikelihoodScore(3);
        request.setImpactScore(4);

        RiskItemResponse response = RiskItemResponse.builder().id(1L).title("Updated Test").build();

        when(riskItemService.updateRiskItem(eq(1L), any(RiskItemRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/risk-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test"));
    }

    @Test
    void deleteRiskItem_Success() throws Exception {
        doNothing().when(riskItemService).deleteRiskItem(1L);

        mockMvc.perform(delete("/api/risk-items/1"))
                .andExpect(status().isNoContent());

        verify(riskItemService, times(1)).deleteRiskItem(1L);
    }
}
