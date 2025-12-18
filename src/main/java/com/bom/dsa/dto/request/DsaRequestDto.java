package com.bom.dsa.dto.request;

import com.bom.dsa.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DsaRequestDto {

    private String name;
    private String mobileNumber;
    private String email;
    private String category;
    private String city;

    // Address
    private String addressLine1;

    // Business Details
    private String constitution;
    private LocalDate registrationDate;
    private String gstin;
    private String pan;

    // Agreement Details
    private LocalDate empanelmentDate;
    private LocalDate agreementDate;
    private LocalDate agreementExpiryDate;
    private String agreementPeriod;
    private String zoneMapping;

    // Risk
    private Double riskScore;

    // Products
    private List<ProductType> products;

    // Bank Details
    private BankDetailsDto bankDetails;

    // Documents (Metadata for uploaded docs)
    private List<DocumentDto> documents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BankDetailsDto {
        private String accountName;
        private String accountNumber;
        private String ifscCode;
        private String branchName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentDto {
        private String documentName; // "MOU", "PAN", etc.
        private String fileName;
        private String filePath; // This would typically come from an upload service
    }
}
