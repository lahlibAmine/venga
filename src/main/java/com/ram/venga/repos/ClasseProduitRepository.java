package com.ram.venga.repos;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.domain.ClasseReservation;
import com.ram.venga.domain.Segment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ClasseProduitRepository extends JpaRepository<ClasseProduit, Long> {

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT C FROM ClasseProduit C " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "(LOWER(C.libelle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.code) LIKE LOWER(CONCAT('%', :keyword, '%')) )) " +
            "ORDER BY C.dateCreated DESC")
    Page<ClasseProduit> findByKeyWord(String keyword, Pageable pageable);
    Optional<ClasseProduit> findByCode(String code);
    Optional<ClasseProduit> findByLibelle(String libelle);
    @Query("SELECT C FROM ClasseProduit C WHERE C.code = :code")
    Optional<ClasseProduit> findByCodeOptional(String code);

    @Query("select cp from ClasseProduit cp where cp.id in :classeProduit")
    Optional<Set<ClasseProduit>> findByIdIn(List<Long> classeProduit);
}
