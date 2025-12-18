package com.bom.dsa.service;

import com.bom.dsa.dto.response.DashboardAnalyticsResponse;
import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.exception.CustomExceptions;
import com.bom.dsa.repository.LeadRepository;
import com.bom.dsa.repository.DsaRepository; // Added import
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for dashboard analytics.
 * Provides aggregated statistics for leads and performance metrics.
 */
@Service
@Slf4j
public class DashboardService {

    private final LeadRepository leadRepository;
    private final DsaRepository dsaRepository; // Added DsaRepository

    public DashboardService(LeadRepository leadRepository, DsaRepository dsaRepository) { // Updated constructor
        this.leadRepository = leadRepository;
        this.dsaRepository = dsaRepository;
    }

    public Mono<DashboardAnalyticsResponse> getDashboardAnalytics(String username) {
        log.info("Fetching dashboard analytics for user: {}", username);

        return Mono.fromCallable(() -> {
            try {
                // Lead counts by status
                Long totalLeads = safeCount(leadRepository.countByCreatedByAndIsDeletedFalse(username));
                Long appliedLeads = safeCount(
                        leadRepository.countByCreatedByAndStatusAndIsDeletedFalse(username, LeadStatus.APPLIED));
                Long underProcessLeads = safeCount(
                        leadRepository.countByCreatedByAndStatusAndIsDeletedFalse(username, LeadStatus.UNDER_PROCESS));
                Long sanctionedLeads = safeCount(
                        leadRepository.countByCreatedByAndStatusAndIsDeletedFalse(username, LeadStatus.SANCTIONED));
                Long disbursedLeads = safeCount(
                        leadRepository.countByCreatedByAndStatusAndIsDeletedFalse(username, LeadStatus.DISBURSED));
                Long rejectedLeads = safeCount(
                        leadRepository.countByCreatedByAndStatusAndIsDeletedFalse(username, LeadStatus.REJECTED));

                log.debug(
                        "Lead counts - total: {}, applied: {}, underProcess: {}, sanctioned: {}, disbursed: {}, rejected: {}",
                        totalLeads, appliedLeads, underProcessLeads, sanctionedLeads, disbursedLeads, rejectedLeads);

                // Product-wise distribution
                Map<String, Long> leadsByProductType = new HashMap<>();
                try {
                    List<Object[]> productTypeResults = leadRepository.countByCreatedByGroupByProductType(username);
                    for (Object[] result : productTypeResults) {
                        if (result[0] != null && result[1] != null) {
                            leadsByProductType.put(result[0].toString(), (Long) result[1]);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error fetching product type distribution: {}", e.getMessage());
                }

                // Status-wise distribution
                Map<String, Long> leadsByStatus = new HashMap<>();
                try {
                    List<Object[]> statusResults = leadRepository.countByCreatedByGroupByStatus(username);
                    for (Object[] result : statusResults) {
                        if (result[0] != null && result[1] != null) {
                            leadsByStatus.put(result[0].toString(), (Long) result[1]);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error fetching status distribution: {}", e.getMessage());
                }

                // Calculate conversion rate
                String conversionRate = "0%";
                if (totalLeads > 0) {
                    double rate = (disbursedLeads * 100.0) / totalLeads;
                    conversionRate = String.format("%.1f%%", rate);
                }

                return DashboardAnalyticsResponse.builder()
                        .totalLeads(totalLeads)
                        .appliedLeads(appliedLeads)
                        .underProcessLeads(underProcessLeads)
                        .sanctionedLeads(sanctionedLeads)
                        .disbursedLeads(disbursedLeads)
                        .rejectedLeads(rejectedLeads)
                        .leadsByProductType(leadsByProductType)
                        .leadsByStatus(leadsByStatus)
                        .conversionRate(conversionRate)
                        .build();

            } catch (Exception e) {
                log.error("Error fetching dashboard analytics for user: {}", username, e);
                throw new CustomExceptions.BusinessException("Failed to fetch dashboard analytics: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get admin dashboard analytics (all users).
     * 
     * @return Mono containing admin dashboard analytics
     */
    public Mono<DashboardAnalyticsResponse> getAdminDashboardAnalytics() {
        log.info("Fetching admin dashboard analytics");

        return Mono.fromCallable(() -> {
            try {
                Long totalLeads = leadRepository.count();
                Long appliedLeads = safeCount(leadRepository.countByStatusAndIsDeletedFalse(LeadStatus.APPLIED));
                Long underProcessLeads = safeCount(
                        leadRepository.countByStatusAndIsDeletedFalse(LeadStatus.UNDER_PROCESS));
                Long sanctionedLeads = safeCount(leadRepository.countByStatusAndIsDeletedFalse(LeadStatus.SANCTIONED));
                Long disbursedLeads = safeCount(leadRepository.countByStatusAndIsDeletedFalse(LeadStatus.DISBURSED));
                Long rejectedLeads = safeCount(leadRepository.countByStatusAndIsDeletedFalse(LeadStatus.REJECTED));

                log.debug(
                        "Admin dashboard - total: {}, applied: {}, underProcess: {}, sanctioned: {}, disbursed: {}, rejected: {}",
                        totalLeads, appliedLeads, underProcessLeads, sanctionedLeads, disbursedLeads, rejectedLeads);

                // Calculate conversion rate
                String conversionRate = "0%";
                if (totalLeads != null && totalLeads > 0) {
                    double rate = (disbursedLeads * 100.0) / totalLeads;
                    conversionRate = String.format("%.1f%%", rate);
                }

                // DSA Statistics
                Long totalDsas = dsaRepository.count();
                Long empanelledDsas = safeCount(dsaRepository.countByStatus(com.bom.dsa.enums.DsaStatus.EMPANELLED));
                Long pendingDsas = safeCount(dsaRepository.countByStatus(com.bom.dsa.enums.DsaStatus.PENDING));
                Long activeDsas = safeCount(dsaRepository.countActiveDsas());
                Long inactiveDsas = safeCount(dsaRepository.countInactiveDsas());

                // Product-wise DSA distribution
                Map<String, Long> productWiseDsaCount = new HashMap<>();
                try {
                    List<Object[]> dsaProductResults = dsaRepository.countDsaByProductType();
                    for (Object[] result : dsaProductResults) {
                        if (result[0] != null && result[1] != null) {
                            productWiseDsaCount.put(result[0].toString(), (Long) result[1]);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error fetching product-wise DSA distribution: {}", e.getMessage());
                }

                // Default total sanctioned amount as requested
                BigDecimal totalSanctionedAmount = new BigDecimal("1000000.0");

                return DashboardAnalyticsResponse.builder()
                        .totalLeads(totalLeads != null ? totalLeads : 0L)
                        .appliedLeads(appliedLeads)
                        .underProcessLeads(underProcessLeads)
                        .sanctionedLeads(sanctionedLeads)
                        .disbursedLeads(disbursedLeads)
                        .rejectedLeads(rejectedLeads)
                        .conversionRate(conversionRate)
                        .totalDsaCount(totalDsas)
                        .empanelledDsaCount(empanelledDsas)
                        .pendingDsaCount(pendingDsas)
                        .activeDsaCount(activeDsas)
                        .inactiveDsaCount(inactiveDsas)
                        .productWiseDsaCount(productWiseDsaCount)
                        .totalSanctionedAmount(totalSanctionedAmount)
                        .build();

            } catch (Exception e) {
                log.error("Error fetching admin dashboard analytics", e);
                throw new CustomExceptions.BusinessException(
                        "Failed to fetch admin dashboard analytics: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get status distribution for charts.
     * 
     * @param createdBy the DSA username
     * @return Mono containing status distribution map
     */
    public Mono<Map<String, Long>> getStatusDistribution(String createdBy) {
        log.debug("Fetching status distribution for user: {}", createdBy);

        return Mono.fromCallable(() -> {
            try {
                Map<String, Long> distribution = new HashMap<>();
                List<Object[]> results = leadRepository.countByCreatedByGroupByStatus(createdBy);
                for (Object[] result : results) {
                    if (result[0] != null && result[1] != null) {
                        distribution.put(result[0].toString(), (Long) result[1]);
                    }
                }
                log.debug("Status distribution fetched: {} entries", distribution.size());
                return distribution;
            } catch (Exception e) {
                log.error("Error fetching status distribution: {}", e.getMessage());
                throw new CustomExceptions.BusinessException("Failed to fetch status distribution: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get product distribution for charts.
     * 
     * @param createdBy the DSA username
     * @return Mono containing product distribution map
     */
    public Mono<Map<String, Long>> getProductDistribution(String createdBy) {
        log.debug("Fetching product distribution for user: {}", createdBy);

        return Mono.fromCallable(() -> {
            try {
                Map<String, Long> distribution = new HashMap<>();
                List<Object[]> results = leadRepository.countByCreatedByGroupByProductType(createdBy);
                for (Object[] result : results) {
                    if (result[0] != null && result[1] != null) {
                        distribution.put(result[0].toString(), (Long) result[1]);
                    }
                }
                log.debug("Product distribution fetched: {} entries", distribution.size());
                return distribution;
            } catch (Exception e) {
                log.error("Error fetching product distribution: {}", e.getMessage());
                throw new CustomExceptions.BusinessException("Failed to fetch product distribution: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Safe count helper - returns 0 if null.
     */
    private Long safeCount(Long value) {
        return value != null ? value : 0L;
    }
}
