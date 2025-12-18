package com.bom.dsa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response DTO for dashboard analytics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAnalyticsResponse {

    // Lead counts
    private Long totalLeads;
    private Long appliedLeads;
    private Long underProcessLeads;
    private Long sanctionedLeads;
    private Long disbursedLeads;
    private Long rejectedLeads;

    // Amount statistics
    private BigDecimal totalAmountRequested;
    private BigDecimal totalAmountDisbursed;

    // Distribution maps
    private Map<String, Long> leadsByProductType;
    private Map<String, Long> leadsByStatus;

    // Admin Analytics
    private BigDecimal totalSanctionedAmount;
    private Long totalDsaCount;
    private Long empanelledDsaCount;
    private Long pendingDsaCount;
    private Long activeDsaCount;
    private Long inactiveDsaCount;
    private Map<String, Long> productWiseDsaCount;

    // DSA Conversion Rate
    private String conversionRate;
}
