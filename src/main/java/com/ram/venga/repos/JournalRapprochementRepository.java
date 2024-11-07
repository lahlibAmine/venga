package com.ram.venga.repos;

import com.ram.venga.domain.JournalRapprochement;
import com.ram.venga.domain.RecetteBrute;
import com.ram.venga.domain.Vente;
import com.ram.venga.model.enumeration.StatutRapprochementEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface JournalRapprochementRepository extends JpaRepository<JournalRapprochement, Long> {
    @Query("select J from JournalRapprochement J where J.vente = :vente and J.recette = :recetteBrute")
    Optional<List<JournalRapprochement>> findByVenteAndRecette(Vente vente, RecetteBrute recetteBrute);

    Optional<JournalRapprochement> findByRecette(RecetteBrute recetteBrute);

    @Query("select J from JournalRapprochement J where J.statut = 'TRAITE'")
    List<JournalRapprochement> findByStatut();
    @Query("select J from JournalRapprochement J where J.statut = 'TRAITE' and J.vente.venteRapproche = true and J.recette.recetteRapproche = true ")
    List<JournalRapprochement> findByStatutandRapprochere();

    @Query("SELECT jr FROM JournalRapprochement jr " +
            "LEFT JOIN jr.vente v " + // Perform a LEFT JOIN with vente
            "WHERE " +
            "(lower(jr.recette.numBillet) LIKE lower(CONCAT('%', :numBillet, '%')) OR :numBillet = '') and " +
            "( :idEntite is null) and " +
            " (:statut is null or jr.statut = :statut) and " +
            " ((:rapprocher is null) or (v.statutVente  = :rapprocher ) or (v is null)) and " +
          //  " (:rapprocher is null or (v.venteRapproche = :rapprocher and v is not null)) and " + // Exclude where vente is null
            " (cast(:dateTimeDebut as date) IS NULL OR jr.date >= :dateTimeDebut) " +
            "   AND (cast(:dateTimeFin as date) IS NULL OR jr.date <= :dateTimeFin) " +
            "   AND (jr.numBillet IN (SELECT MIN(subJr.numBillet) FROM JournalRapprochement subJr GROUP BY subJr.numBillet))")
    List<JournalRapprochement> findDistinctByVente(
            @Param("numBillet") String numBillet,
            @Param("dateTimeDebut") LocalDateTime dateTimeDebut,
            @Param("dateTimeFin") LocalDateTime dateTimeFin,
            @Param("statut") StatutRapprochementEnum status,
            @Param("rapprocher") StatutVenteEnum rapprocher,
            @Param("idEntite") List<Long> idEntite
            );






    @Query("SELECT J.recette FROM JournalRapprochement J WHERE J.vente.id = :id AND (J.statut = :statut OR :statut IS NULL) AND (lower(J.recette.numBillet) LIKE lower(CONCAT('%', :numBillet, '%')) OR :numBillet = '')")
    List<RecetteBrute> findByRecettewithVente(Long id, @Param("statut") StatutRapprochementEnum statut,@Param("numBillet") String numBillet);

    @Query("select distinct J.recette from JournalRapprochement J where J.vente is null")
    List<RecetteBrute> findByRecettewithVenteNull();

    @Query("select J from JournalRapprochement J where J.vente is not null and J.recette = :recetteBrute")
    Optional<List<JournalRapprochement>> findByRecetteWithVente( RecetteBrute recetteBrute);
    @Query("select J from JournalRapprochement J where J.vente is null and J.recette = :recetteBrute")
    Optional<List<JournalRapprochement>> findByRecetteWithVenteNull( RecetteBrute recetteBrute);
    @Modifying
    @Transactional
    @Query("UPDATE JournalRapprochement r SET r.isArchived = true WHERE r.numBillet IN :numBillet")
    void updateArchivedjournalRapprochementBetweenDates( List<String> numBillet);
}
