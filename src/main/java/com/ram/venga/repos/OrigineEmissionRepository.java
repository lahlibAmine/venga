package com.ram.venga.repos;

import com.ram.venga.domain.OrigineEmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface OrigineEmissionRepository extends JpaRepository<OrigineEmission, Long> {

    boolean existsByNomIgnoreCase(String nom);
    @Query("SELECT O FROM OrigineEmission O " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "(LOWER(O.devise.devise) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(O.pays.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(O.pays.codeIso) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          //  " TO_CHAR(O.nbrPointBienvenue, 'FM999999999999999999999999') LIKE CONCAT('%', :keyword, '%') or " +
            "LOWER(O.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) )) " +
            "ORDER BY O.dateCreated DESC")
    Page<OrigineEmission> findAllWithKeywordDevise(String keyword, Pageable pageable);

    @Query("SELECT O FROM OrigineEmission O " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "(LOWER(O.pays.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(O.pays.codeIso) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          //  " TO_CHAR(O.nbrPointBienvenue, 'FM999999999999999999999999') LIKE CONCAT('%', :keyword, '%') or " +
            "LOWER(O.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) )) " +
            "ORDER BY O.dateCreated DESC")
    Page<OrigineEmission> findAllWithKeyword(String keyword, Pageable pageable);
    @Query("select distinct O from OrigineEmission O where O.pays.id =:id")
    OrigineEmission findByPaysId(Long id);

    @Query("SELECT O FROM OrigineEmission O " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "( TO_CHAR(O.nbrPointBienvenue, 'FM999999999999999999999999') LIKE CONCAT('%', :keyword, '%')) or " +
            "LOWER(O.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) )  " +
            "ORDER BY O.dateCreated DESC")
    Page<OrigineEmission> findAllWithKeywordNbr(String keyword, Pageable pageable);

    OrigineEmission findByNom(String origine);
    @Query("select o from OrigineEmission o where o.nom = :origine and o.id <> :id")
    OrigineEmission findByNomNotId(String origine,Long id);
}
