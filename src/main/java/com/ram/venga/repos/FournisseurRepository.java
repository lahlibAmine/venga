package com.ram.venga.repos;

import com.ram.venga.domain.Fournisseur;
import com.ram.venga.domain.OrigineEmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    boolean existsByNomIgnoreCase(String nom);
    @Query("SELECT F FROM Fournisseur F " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "(LOWER(F.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(F.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(F.telephone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(F.adresse) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "ORDER BY F.dateCreated DESC")
    Page<Fournisseur> findAllWithKeyword(String keyword, Pageable pageable);

    @Query("select distinct F from Fournisseur F join F.cadeauxBAs C where C.origineEmission.id = :idOrigineByUser")
    List<Fournisseur> findAllByOrigineEmission(Long idOrigineByUser);
    @Query("select f from Fournisseur f where f.email = :email or f.nom = :nom")
    Fournisseur findByNomOrEmail(String nom, String email);
}
