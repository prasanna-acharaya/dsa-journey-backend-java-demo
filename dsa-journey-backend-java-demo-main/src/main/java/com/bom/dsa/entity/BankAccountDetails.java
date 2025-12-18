package com.bom.dsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity for DSA Bank Account Details.
 */
@Entity
@Table(name = "dsa_bank_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dsa_id", nullable = false)
    private Dsa dsa;

    @Column(name = "account_name", length = 100)
    private String accountName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "branch_name", length = 100)
    private String branchName;
}
