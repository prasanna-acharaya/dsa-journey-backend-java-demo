package com.bom.dsa.service;

import com.bom.dsa.dto.request.DsaRequestDto;
import com.bom.dsa.dto.response.DsaResponseDto;
import com.bom.dsa.entity.BankAccountDetails;
import com.bom.dsa.entity.Dsa;
import com.bom.dsa.entity.DsaDocument;
import com.bom.dsa.enums.DsaStatus;
import com.bom.dsa.exception.CustomExceptions;
import com.bom.dsa.repository.DsaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DsaService {

    private final DsaRepository dsaRepository;
    private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    public DsaService(DsaRepository dsaRepository,
            org.springframework.transaction.PlatformTransactionManager transactionManager) {
        this.dsaRepository = dsaRepository;
        this.transactionTemplate = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
    }

    public reactor.core.publisher.Mono<DsaResponseDto> createDsa(DsaRequestDto request, String createdBy) {
        log.info("Creating DSA: {}", request.getName());

        return reactor.core.publisher.Mono.fromCallable(() -> {
            return transactionTemplate.execute(status -> {
                // Generate unique code (Simple logic for demo)
                String uniqueCode = "DSA" + System.currentTimeMillis();

                Dsa dsa = Dsa.builder()
                        .name(request.getName())
                        .uniqueCode(uniqueCode)
                        .mobileNumber(request.getMobileNumber())
                        .email(request.getEmail())
                        .status(DsaStatus.PENDING) // Default to Pending
                        .category(request.getCategory())
                        .city(request.getCity())
                        .addressLine1(request.getAddressLine1())
                        .constitution(request.getConstitution())
                        .registrationDate(request.getRegistrationDate())
                        .gstin(request.getGstin())
                        .pan(request.getPan())
                        .empanelmentDate(request.getEmpanelmentDate())
                        .agreementDate(request.getAgreementDate())
                        .agreementExpiryDate(request.getAgreementExpiryDate())
                        .agreementPeriod(request.getAgreementPeriod())
                        .zoneMapping(request.getZoneMapping())
                        .riskScore(request.getRiskScore() != null ? request.getRiskScore() : 0.0)
                        .products(request.getProducts() != null ? request.getProducts() : new ArrayList<>())
                        .createdBy(createdBy)
                        .build();

                // Bank Details
                if (request.getBankDetails() != null) {
                    BankAccountDetails bank = BankAccountDetails.builder()
                            .accountName(request.getBankDetails().getAccountName())
                            .accountNumber(request.getBankDetails().getAccountNumber())
                            .ifscCode(request.getBankDetails().getIfscCode())
                            .branchName(request.getBankDetails().getBranchName())
                            .build();
                    dsa.setBankAccountDetails(bank);
                }

                // Documents
                if (request.getDocuments() != null) {
                    request.getDocuments().forEach(docDto -> {
                        DsaDocument doc = DsaDocument.builder()
                                .documentName(docDto.getDocumentName())
                                .fileName(docDto.getFileName())
                                .filePath(docDto.getFilePath())
                                .build();
                        dsa.addDocument(doc);
                    });
                }

                Dsa savedDsa = dsaRepository.save(dsa);
                return mapToResponse(savedDsa);
            });
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public reactor.core.publisher.Mono<DsaResponseDto> updateDsa(UUID id, DsaRequestDto request) {
        return reactor.core.publisher.Mono.fromCallable(() -> {
            return transactionTemplate.execute(status -> {
                Dsa dsa = dsaRepository.findById(id)
                        .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("DSA", "id", id));

                // Update fields
                dsa.setName(request.getName());
                dsa.setMobileNumber(request.getMobileNumber());
                dsa.setEmail(request.getEmail());
                dsa.setCategory(request.getCategory());
                dsa.setCity(request.getCity());
                dsa.setAddressLine1(request.getAddressLine1());
                dsa.setConstitution(request.getConstitution());
                dsa.setGstin(request.getGstin());
                dsa.setPan(request.getPan());
                dsa.setRegistrationDate(request.getRegistrationDate());
                dsa.setEmpanelmentDate(request.getEmpanelmentDate());
                dsa.setAgreementDate(request.getAgreementDate());
                dsa.setAgreementExpiryDate(request.getAgreementExpiryDate());
                dsa.setAgreementPeriod(request.getAgreementPeriod());
                dsa.setZoneMapping(request.getZoneMapping());
                dsa.setRiskScore(request.getRiskScore() != null ? request.getRiskScore() : dsa.getRiskScore());

                // Update products
                if (request.getProducts() != null) {
                    dsa.setProducts(new ArrayList<>(request.getProducts()));
                }

                // Update Bank Details
                if (request.getBankDetails() != null) {
                    if (dsa.getBankAccountDetails() == null) {
                        BankAccountDetails bank = BankAccountDetails.builder().dsa(dsa).build();
                        dsa.setBankAccountDetails(bank);
                    }
                    dsa.getBankAccountDetails().setAccountName(request.getBankDetails().getAccountName());
                    dsa.getBankAccountDetails().setAccountNumber(request.getBankDetails().getAccountNumber());
                    dsa.getBankAccountDetails().setIfscCode(request.getBankDetails().getIfscCode());
                    dsa.getBankAccountDetails().setBranchName(request.getBankDetails().getBranchName());
                }

                Dsa savedDsa = dsaRepository.save(dsa);
                return mapToResponse(savedDsa);
            });
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public reactor.core.publisher.Mono<DsaResponseDto> getDsaById(UUID id) {
        return reactor.core.publisher.Mono.fromCallable(() -> {
            Dsa dsa = dsaRepository.findById(id)
                    .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("DSA", "id", id));
            return mapToResponse(dsa);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public reactor.core.publisher.Mono<DsaResponseDto> updateDsaStatus(UUID id, DsaStatus status, String remark) {
        return reactor.core.publisher.Mono.fromCallable(() -> {
            return transactionTemplate.execute(txStatus -> {
                Dsa dsa = dsaRepository.findById(id)
                        .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("DSA", "id", id));
                dsa.setStatus(status);
                dsa.setRemark(remark);
                return mapToResponse(dsaRepository.save(dsa));
            });
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public reactor.core.publisher.Mono<Page<DsaResponseDto>> getAllDsas(String category, DsaStatus status,
            Pageable pageable) {
        return reactor.core.publisher.Mono.fromCallable(() -> {
            Specification<Dsa> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (category != null && !category.isEmpty()) {
                    predicates.add(cb.equal(root.get("category"), category));
                }
                if (status != null) {
                    predicates.add(cb.equal(root.get("status"), status));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };

            Page<Dsa> page = dsaRepository.findAll(spec, pageable);
            List<DsaResponseDto> content = page.getContent().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            Page<DsaResponseDto> resultPage = new PageImpl<>(content, pageable, page.getTotalElements());
            return resultPage;
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    private DsaResponseDto mapToResponse(Dsa dsa) {
        DsaResponseDto.BankDetailsDto bankDto = null;
        if (dsa.getBankAccountDetails() != null) {
            bankDto = DsaResponseDto.BankDetailsDto.builder()
                    .accountName(dsa.getBankAccountDetails().getAccountName())
                    .accountNumber(dsa.getBankAccountDetails().getAccountNumber())
                    .ifscCode(dsa.getBankAccountDetails().getIfscCode())
                    .branchName(dsa.getBankAccountDetails().getBranchName())
                    .build();
        }

        List<DsaResponseDto.DocumentDto> docDtos = dsa.getDocuments().stream()
                .map(d -> DsaResponseDto.DocumentDto.builder()
                        .documentName(d.getDocumentName())
                        .fileName(d.getFileName())
                        .filePath(d.getFilePath())
                        .build())
                .collect(Collectors.toList());

        return DsaResponseDto.builder()
                .id(dsa.getId())
                .name(dsa.getName())
                .uniqueCode(dsa.getUniqueCode())
                .mobileNumber(dsa.getMobileNumber())
                .email(dsa.getEmail())
                .status(dsa.getStatus())
                .category(dsa.getCategory())
                .city(dsa.getCity())
                .addressLine1(dsa.getAddressLine1())
                .constitution(dsa.getConstitution())
                .registrationDate(dsa.getRegistrationDate())
                .gstin(dsa.getGstin())
                .pan(dsa.getPan())
                .empanelmentDate(dsa.getEmpanelmentDate())
                .agreementDate(dsa.getAgreementDate())
                .agreementExpiryDate(dsa.getAgreementExpiryDate())
                .agreementPeriod(dsa.getAgreementPeriod())
                .zoneMapping(dsa.getZoneMapping())
                .riskScore(dsa.getRiskScore())
                .remark(dsa.getRemark())
                .products(dsa.getProducts())
                .bankDetails(bankDto)
                .documents(docDtos)
                .build();
    }
}
