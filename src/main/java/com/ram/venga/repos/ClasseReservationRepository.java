package com.ram.venga.repos;

import com.ram.venga.domain.ClasseReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ClasseReservationRepository extends JpaRepository<ClasseReservation, Long> {

    boolean existsByCodeIgnoreCase(String code);
    boolean existsByClasseProduitId(Long id);

    @Query("SELECT C FROM ClasseReservation C " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "(LOWER(C.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.libelle) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "ORDER BY C.dateCreated DESC")
    Page<ClasseReservation> findAllByCodeOrLibelleLike(@Param("keyword") String keyword, Pageable pageable);
    ClasseReservation findByCode(String classeReservation);
}
