package com.ram.venga.repos;

import com.ram.venga.domain.Segment;
import liquibase.pro.packaged.Q;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;


public interface SegmentRepository extends JpaRepository<Segment, Long> {

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT S FROM Segment S " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "(LOWER(S.escaleDepart) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(S.escaleDestination) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(S.code) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "ORDER BY S.code ASC ")
    Page<Segment> findAllByCodeOrEscaleDepartOrEscaleDestinationLike(@Param("keyword") String keyword, Pageable pageable);

    @Query("select S from Segment S where S.escaleDepart =:escaleDepart and S.escaleDestination =:escaleDestination")
    Segment findByEscalDepartAndEscalArriver(String escaleDepart, String escaleDestination);

    @Query("select  S.escaleDestination from Segment S order by S.escaleDestination asc")
    Set<String> findByEscalArriver();
    @Query("select  S.escaleDepart from Segment S order by S.escaleDepart asc")
    Set<String> findByEscalDepart();
    @Query("select S from Segment S where S.code =:code")
    Optional<Segment> findByCode(String code);
}
