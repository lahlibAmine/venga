package com.ram.venga.repos;

import com.ram.venga.domain.RecetteBrute;
import com.ram.venga.model.RecetteBruteDTO;
import com.ram.venga.model.SegmentRecetteDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface RecetteBruteRepository extends JpaRepository<RecetteBrute, Long> {
    @Query("select R.numCoupon from RecetteBrute R where R.numBillet =:numBillet")
    List<String> findAllByNumBillet(String numBillet);

    @Query("select R from RecetteBrute R where R.recetteIntegre = true and( R.recetteRapproche = false or  R.recetteRapproche is null )and R.origineEmission is not null")
    List<RecetteBrute> findAllByIntegre();
    @Query("select R from RecetteBrute R where R.numBillet =:numBillet")
    List<RecetteBrute> findAllRecetteByNumBillet(String numBillet);

    @Query("select R from RecetteBrute R where R.recetteIntegre = true and R.recetteRapproche = true and R.origineEmission is not null")
    List<RecetteBrute> findAllByIntegreAndRapprocher();
    @Query("select R from RecetteBrute R where R.numBillet =:numBillet and R.recetteRapproche = true and R.recetteIntegre = true")
    List<RecetteBrute> findAllByNumBilletIntegerRapprocher(String numBillet);

    @Query("select R from RecetteBrute R where R.numBillet in :numBillet")
    List<RecetteBrute> findAllByListNumBillet(List<String> numBillet);

    @Query("select R from RecetteBrute R where R.numBillet = :numBillet ")
    List<RecetteBrute> findAllByNumBilletList(String numBillet);
    @Query("select R from RecetteBrute R where R.recetteRapproche = true and (cast(:dateTimeDebut as date) IS NULL OR R.dateTransport >= :dateTimeDebut ) AND (cast(:dateTimeFin as date) IS NULL OR R.dateTransport <= :dateTimeFin) ")
    List<RecetteBrute> findAllByRapprocher(LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin);

    @Query("select new com.ram.venga.model.SegmentRecetteDTO(R.escaleDepart,R.escaleArrivee,R.numCoupon,R.dateTransport" +
            "  ,R.recetteRapproche,R.recetteIntegre,R.montantBrut,R.cieVol,R.classeReservation,o.debit,R.motif,cr.classeProduit.code ,R.codeIATA,R.numBillet ) " +
            " from RecetteBrute R left join Opperation o on o.recetteBrute = R left join ClasseReservation cr on cr.code = R.classeReservation where R.numBillet in :numBillet and (:classeR is null or :classeR = '' or (lower(R.classeReservation) like lower(concat('%',:classeR,'%'))) and (R.classeReservation <> ' ' or R.classeReservation is not null))" +
            "and (:origine is null or :origine = '' or lower(R.escaleDepart) like lower(concat('%',:origine,'%') ) )" +
            "AND (R.montantBrut >= :montantDebut or :montantDebut = 0.0  ) " +
            "AND (R.montantBrut <= :montantFin or :montantFin = 0.0) " +
            "and (:destination is null or :destination = '' or lower(R.escaleArrivee) like lower(concat('%',:destination,'%') ) )" )
    List<SegmentRecetteDTO> findAllByNumBilletListByFilter(List<String> numBillet, String classeR, String origine, String destination, Double montantDebut, Double montantFin);

    @Query("select R from RecetteBrute R where R.recetteIntegre = false ")
    List<RecetteBrute> findRecetteNonInteger();
    @Query("select R from RecetteBrute R where R.numCoupon = :numCoupon and R.numBillet = :numBillet")
    Optional<RecetteBrute> findByNumBilletAndCoupon(String numCoupon, String numBillet);

    @Query("select R from RecetteBrute R where R.recetteIntegre = false ")
    List<RecetteBrute> findAllRecetteBruteNonInteger();
    @Modifying
    @Transactional
    @Query("UPDATE RecetteBrute r SET r.isArchived = true WHERE r.numBillet IN :numBillet")
    void updateArchivedRecetteBetweenDates( List<String> numBillet);

    @Query("select R from RecetteBrute R where R.numBillet in :numBillet ")
    List<RecetteBrute> findAllByNumBilletListIn(List<String> numBillet);

    @Query("select R from RecetteBrute R  where R.numBillet in :numBillet ")
    List<RecetteBrute> findAllByListNumBilletWithDebit(List<String> numBillet);

}
