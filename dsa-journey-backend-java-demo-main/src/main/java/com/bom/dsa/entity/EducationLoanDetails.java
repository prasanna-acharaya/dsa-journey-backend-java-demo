package com.bom.dsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity for Education Loan specific details.
 * One-to-one relationship with Lead entity.
 */
@Entity
@Table(name = "education_loan_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationLoanDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false, unique = true)
    private Lead lead;

    @Column(name = "amount_requested", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal amountRequested = BigDecimal.ZERO;

    @Column(name = "repayment_period", nullable = false)
    @Builder.Default
    private Integer repaymentPeriod = 0;

    @Column(name = "course_name", length = 255)
    private String courseName;

    @Column(name = "institution_name", length = 255)
    private String institutionName;

    @Column(name = "institution_country", length = 100)
    private String institutionCountry;

    @Column(name = "institution_state", length = 100)
    private String institutionState;

    @Column(name = "institution_city", length = 100)
    private String institutionCity;

    @Column(name = "course_duration_years", nullable = false)
    @Builder.Default
    private Integer courseDurationYears = 0;
}
