package com.riskheatmap.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RiskItemTest {

    @Test
    void testPrePersist() {
        RiskItem item = new RiskItem();
        item.prePersist();
        
        assertEquals(false, item.getIsDeleted());
        assertEquals("OPEN", item.getStatus());
    }

    @Test
    void testPreUpdate() {
        RiskItem item = new RiskItem();
        item.setLikelihoodScore(4);
        item.setImpactScore(5);
        
        item.preUpdate();
        
        assertEquals(20, item.getRiskScore());
    }
}
