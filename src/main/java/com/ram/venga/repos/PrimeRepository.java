package com.ram.venga.repos;

import com.ram.venga.domain.Prime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface PrimeRepository extends JpaRepository<Prime, Long> {

    @Query("SELECT P FROM Prime P " +
            "WHERE (:origin IS NULL OR :origin = '' OR " +
            "LOWER(P.origineEmission.nom) LIKE LOWER(CONCAT('%', :origin, '%'))) " +
            "and ( :keyword is null or TO_CHAR( P.nbrPoint, 'FM999999999999999999999999') LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "AND (:classeProduit IS NULL OR :classeProduit = '' OR " +
            "LOWER(P.classeProduit.code) LIKE LOWER(CONCAT('%', :classeProduit, '%'))) " +
            "AND (:origineS IS NULL OR :origineS = '' OR " +
            "LOWER(P.segment.escaleDepart) LIKE LOWER(CONCAT('%', :origineS, '%'))) " +
            "AND (:destinationS IS NULL OR :destinationS = '' OR " +
            "LOWER(P.segment.escaleDestination) LIKE LOWER(CONCAT('%', :destinationS, '%'))) " +
            "ORDER BY P.dateCreated ASC")
    Page<Prime> findAllByKeyWord(Pageable pageable, String origin, String classeProduit, String keyword, String origineS, String destinationS);

    @Query("select P from Prime P where P.origineEmission.id = :id and P.classeProduit.code = :classeProduit and P.segment.id =:segement")
    Prime findByOrigineEmissionIdAndclassProduitAndSegement(Long id,String classeProduit,Long segement);

    @Query("select P from Prime P where P.origineEmission.id = :id and P.classeProduit.id = :classeProduit and P.segment.id =:segement ")
    Prime findByOrigineEmissionIdAndclassProduitIdAndSegement(Long id,Long classeProduit,Long segement);

    boolean existsByClasseProduitId(Long id);

    boolean existsBySegmentId(Long id);

    boolean existsByOrigineEmissionIdAndClasseProduitIdAndSegmentId(Long oe,Long cp,Long s);

    @Query("SELECT " +
            "CASE WHEN COUNT(P) > 0 THEN true ELSE false END " +
            "FROM Prime P " +
            "WHERE P.origineEmission.id = :origineEmissionId " +
            "AND P.classeProduit.id = :classProduitId " +
            "AND P.segment = :segmentId " +
            "AND P.id <> :id")
    boolean existsByOrigineEmissionIdAndClasseProduitIdAndSegmentIdAndIdNot(Long origineEmissionId, Long classProduitId, Long segmentId, Long id);

    @Query("SELECT P " +
            "FROM Prime P " +
            "WHERE P.origineEmission.id = :origineEmissionId " +
            "AND P.classeProduit.id = :classProduitId " +
            "AND P.segment.id = :segmentId")
    Optional<Prime> findByOrigineEmissionIdAndClasseProduitIdAndSegment(Long origineEmissionId, Long classProduitId, Long segmentId);

    @Query("SELECT P " +
            "FROM Prime P " +
            "WHERE P.origineEmission.id = :origineEmissionId " +
            "AND P.classeProduit.code = :classProduit " +
            "AND P.segment.code = :segment")
    Optional<Prime> findByOrigineEmissionIdAndClasseProduitCodeAndSegmentCode(Long origineEmissionId, String classProduit, String segment);

}
