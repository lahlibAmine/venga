package com.ram.venga.repos;

import com.ram.venga.domain.CategorieCadeau;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CategorieCadeauRepository extends JpaRepository<CategorieCadeau, Long> {

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT C FROM CategorieCadeau C " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "(LOWER(C.libelle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.code) LIKE LOWER(CONCAT('%', :keyword, '%')) )) " +
            "ORDER BY C.dateCreated DESC")
    Page<CategorieCadeau> findAllByKeyword(String keyword, Pageable pageable);
}
