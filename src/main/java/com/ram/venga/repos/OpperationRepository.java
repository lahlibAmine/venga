package com.ram.venga.repos;

import com.ram.venga.domain.BonCommande;
import com.ram.venga.domain.Opperation;
import com.ram.venga.domain.RecetteBrute;
import com.ram.venga.domain.Vente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface OpperationRepository extends JpaRepository<Opperation, Long> {
    @Query("select op from Opperation op  where op.recetteBrute in :recetteBrutes and op.debit <> 0")
    List<Opperation> findByRecetteBruteIsIn(List<RecetteBrute> recetteBrutes);

   @Query("select O from Opperation O where O.bonCommande.agentCommercial.collaborateur.id = :id ")
    List<Opperation> findByBonCommandeAgentCommercialId(Long id);
   List<Opperation> findBySignature(String signature);
   @Query("select SUM (o.debit) from Opperation o where o.recetteBrute.numBillet = :numBillet")
   Double sumSolde(String numBillet);
    @Query("SELECT SUM(o.recetteBrute.montantBrut) FROM Opperation o WHERE o.recetteBrute.numBillet = :numBillet")
    Double sumMontantBrute(@Param("numBillet") String numBillet);

    @Query("SELECT SUM(o.recetteBrute.montantBrut) FROM Opperation o WHERE o.recetteBrute.numBillet = :numBillet and o.recetteBrute.recetteRapproche = true and (cast(:dateTimeDebut as date) IS NULL OR o.recetteBrute.dateTransport >= :dateTimeDebut ) AND (cast(:dateTimeFin as date) IS NULL OR o.recetteBrute.dateTransport <= :dateTimeFin) ")
    Double sumMontantBruteByDate(@Param("numBillet") String numBillet,LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin);

    Opperation findByRecetteBruteId(Long recetteBrute);

    @Query("select Sum(o.recetteBrute.montantBrut) from Opperation o where o.signature = :signature and o.recetteBrute.recetteRapproche = true and (cast(:dateTimeDebut as date) IS NULL OR o.recetteBrute.dateTransport >= :dateTimeDebut ) AND (cast(:dateTimeFin as date) IS NULL OR o.recetteBrute.dateTransport <= :dateTimeFin) ")
    Double findBySignatureWithFilterDate(String signature,LocalDateTime dateTimeDebut,LocalDateTime dateTimeFin);

    @Query("select o from Opperation o where o.recetteBrute in :recetteBrutes")
    List<Opperation> findByRecetteBrutes(List<RecetteBrute> recetteBrutes);

}
