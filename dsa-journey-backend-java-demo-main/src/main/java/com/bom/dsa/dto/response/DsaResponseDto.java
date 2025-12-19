package com.bom.dsa.dto.response;

import com.bom.dsa.enums.DsaStatus;
import com.bom.dsa.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DsaResponseDto {

    private UUID id;
    private String name;
    private String uniqueCode;
    private String mobileNumber;
    private String email;
    private DsaStatus status;
    private String category;
    private String city;
    private String addressLine1;

    private String constitution;
    private LocalDate registrationDate;
    private String gstin;
    private String pan;

    private LocalDate empanelmentDate;
    private LocalDate agreementDate;
    private LocalDate agreementExpiryDate;
    private String agreementPeriod;
    private String zoneMapping;

    private Double riskScore;
    private String remark;

    private List<ProductType> products;
    private BankDetailsDto bankDetails;
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
        private String documentName;
        private String fileName;
        private String filePath;
    }
}
