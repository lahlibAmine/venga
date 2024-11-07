package com.ram.venga.repos;

import com.ram.venga.domain.JournalRapprochement;
import com.ram.venga.domain.JournalRapprochementView;
import com.ram.venga.model.enumeration.StatutRapprochementEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface JournalRapprochementViewRepository extends JpaRepository<JournalRapprochementView, Long> {

    @Query("SELECT jr FROM JournalRapprochementView jr " +
            "WHERE " +
            "jr.isArchived = :isArchived and" +
            "(lower(jr.numBillet) LIKE lower(CONCAT('%', :numBillet, '%')) OR :numBillet = '') and " +
         //   "( :idEntite is null) and " +
//            " (:statut is null or jr.statut = :statut) and " +
            " ((:rapprocher is null) or (jr.statutVente  = :rapprocher ) ) and " +
            //  " (:rapprocher is null or (v.venteRapproche = :rapprocher and v is not null)) and " + // Exclude where vente is null
            " (cast(:dateTimeDebut as date) IS NULL OR jr.lastUpdated >= :dateTimeDebut) " +
            "   AND (cast(:dateTimeFin as date) IS NULL OR jr.lastUpdated <= :dateTimeFin) " )
    Page<JournalRapprochementView> findDistinctByVente(
            @Param("numBillet") String numBillet,
            @Param("dateTimeDebut") LocalDate dateTimeDebut,
            @Param("dateTimeFin") LocalDate dateTimeFin,
            @Param("rapprocher") StatutVenteEnum rapprocher,
            Boolean isArchived,
            Pageable pageable
    );
}
