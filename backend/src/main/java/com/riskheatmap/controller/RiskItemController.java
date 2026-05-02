package com.riskheatmap.controller;

import com.riskheatmap.dto.RiskItemRequest;
import com.riskheatmap.dto.RiskItemResponse;
import com.riskheatmap.service.RiskItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/risk-items")
@RequiredArgsConstructor
public class RiskItemController {

    private final RiskItemService riskItemService;

    // GET /all paginated
    @GetMapping("/all")
    public ResponseEntity<Page<RiskItemResponse>> getAllRiskItems(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<RiskItemResponse> response = riskItemService.getAllActiveRiskItems(pageable);
        return ResponseEntity.ok(response);
    }

    // GET /{id} with 404 handling (handled by GlobalExceptionHandler)
    @GetMapping("/{id}")
    public ResponseEntity<RiskItemResponse> getRiskItemById(@PathVariable Long id) {
        RiskItemResponse response = riskItemService.getRiskItemById(id);
        return ResponseEntity.ok(response);
    }

    // POST /create with @Valid
    @PostMapping("/create")
    public ResponseEntity<RiskItemResponse> createRiskItem(
            @Valid @RequestBody RiskItemRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "system";
        RiskItemResponse response = riskItemService.createRiskItem(request, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiskItemResponse> updateRiskItem(
            @PathVariable Long id, @Valid @RequestBody RiskItemRequest request) {
        RiskItemResponse response = riskItemService.updateRiskItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRiskItem(@PathVariable Long id) {
        riskItemService.deleteRiskItem(id);
        return ResponseEntity.noContent().build();
    }
}
