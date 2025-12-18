package com.bom.dsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity for DSA Documents (MOU, etc.)
 */
@Entity
@Table(name = "dsa_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DsaDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dsa_id", nullable = false)
    private Dsa dsa;

    @Column(name = "document_name", length = 100)
    private String documentName; // e.g., "MOU Document", "Risk Assessment"

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "uploaded_at", nullable = false)
    @Builder.Default
    private Instant uploadedAt = Instant.now();
}
