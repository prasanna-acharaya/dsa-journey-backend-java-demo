package com.bom.dsa.repository;

import com.bom.dsa.entity.Dsa;
import com.bom.dsa.enums.DsaStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DsaRepository extends JpaRepository<Dsa, UUID>, JpaSpecificationExecutor<Dsa> {

    boolean existsByUniqueCode(String uniqueCode);

    long countByStatus(DsaStatus status);

    // For Active/Inactive counts - assuming EMPANELLED is Active, others Inactive?
    // Or we can add an isActive field later. For now, let's map Status to Concept.
    // User requested "Active" and "Inactive".
    // Let's assume EMPANELLED = Active, REJECTED/RETURNED/INACTIVE = Inactive

    @Query("SELECT COUNT(d) FROM Dsa d WHERE d.status = 'EMPANELLED'")
    long countActiveDsas();

    @Query("SELECT COUNT(d) FROM Dsa d WHERE d.status IN ('REJECTED', 'RETURNED', 'INACTIVE')")
    long countInactiveDsas();

    @Query("SELECT p, COUNT(d) FROM Dsa d JOIN d.products p GROUP BY p")
    List<Object[]> countDsaByProductType();
}
