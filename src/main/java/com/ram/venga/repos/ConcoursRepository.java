package com.ram.venga.repos;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.domain.Concours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


public interface ConcoursRepository extends JpaRepository<Concours, Long> {

    boolean existsByLibelleIgnoreCase(String libelle);

    @Query("SELECT distinct C FROM Concours C left join C.classeProduits cp " +
            "WHERE (:origin IS NULL OR :origin = '' OR " +
            "LOWER(C.origineEmission.nom) LIKE LOWER(CONCAT('%', :origin, '%'))) " +
            "AND ((:classeProduits) IS NULL or " +
            "LOWER(cp.code) in  (:classeProduits)) " +
            "ORDER BY C.dateCreated DESC")
    Page<Concours> findByKeyword(String origin, List<String> classeProduits, Pageable pageable);
    @Query("SELECT DISTINCT C FROM Concours C JOIN C.classeProduits cp " +
            "WHERE lower(cp.code) = :libelle  and C.origineEmission.id = :idOrigin " +
            "AND (" +
            "     (  C.dateDebut IS  NULL " +
            "   AND C.dateFin IS  NULL )" +
            "   or( cast(:dateTrons as date) BETWEEN cast(C.dateDebut as date) AND cast(C.dateFin as date)) " +
            ") " +
            "and( " +
            "      ( C.dateDebutVente IS  NULL " +
            "   AND C.dateFinVente IS  NULL) " +
            "   or (cast(:dateEmis as date) BETWEEN cast(C.dateDebutVente as date) AND cast(C.dateFinVente as date)) " +
            ")")
    Concours findByClasseProduitLibelle(String libelle, LocalDate dateTrons, LocalDate dateEmis,Long idOrigin);
    @Query("SELECT distinct C FROM Concours C JOIN C.classeProduits cp " +
            "WHERE C.origineEmission.id = :idOrigin and (C.dateDebut <= :dateFinDTO) " +
            "AND (C.dateFin >= :dateDebutDTO) " +
            "AND cp.id IN :code " +
            "AND (C.id <> :id OR :id IS NULL)")
    List<Concours> findOverlappingConcours(LocalDate dateDebutDTO, LocalDate dateFinDTO, Set<Long> code, Long id,Long idOrigin);
}
