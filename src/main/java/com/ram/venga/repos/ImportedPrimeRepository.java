package com.ram.venga.repos;

import com.ram.venga.domain.ImportedPrimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import com.ram.venga.model.enumeration.ScheduleStatus;

public interface ImportedPrimeRepository extends JpaRepository<ImportedPrimes, Long> {

    @Query("SELECT IP " +
            "FROM ImportedPrimes IP " +
            "WHERE IP.schedule_status = 'NOT_YET'")
    List<ImportedPrimes> findAllNotTreated();

    @Query("SELECT P FROM ImportedPrimes P " +
            "WHERE P.origineEmission.id = :origineEmissionId " +
            "AND P.classeProduit = :classProduit " +
            "AND P.segment = :segment")
    Optional<ImportedPrimes> findByOrigineEmissionIdAndClassProduitAndSegment(Long origineEmissionId, String classProduit, String segment);

    List<ImportedPrimes> findByOrigineEmissionId(Long id);
        @Query("SELECT IP " +
            "FROM ImportedPrimes IP " +
            "WHERE IP.processing_status = 'Failure'" +
                "AND IP.fileId = :fileId")
    List<ImportedPrimes> findAllFailureImportedPrimes(String fileId);
     @Query("SELECT COUNT(ip) FROM ImportedPrimes ip WHERE ip.schedule_status = :scheduleStatus AND ip.fileId = :fileId")
    Long countImportedPrimesByScheduleStatusAndFileId(ScheduleStatus scheduleStatus, String fileId);

    long countImportedPrimesByFileId (String fileId);
}