package com.ram.venga.repos;

import com.ram.venga.domain.ExportEmissionView;
import com.ram.venga.domain.JournalRapprochement;
import com.ram.venga.domain.RecetteBrute;
import com.ram.venga.domain.Vente;
import com.ram.venga.model.enumeration.StatutRapprochementEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ExportEmissionRepository extends JpaRepository<ExportEmissionView, Long> {
    @Query("select E from ExportEmissionView E LEFT JOIN E.entite ee " +
            "LEFT JOIN ee.parent ep " +
            "LEFT JOIN ep.parent epp where  E.classeReservation <> ' ' and E.entite is not null  and E.collaborateur.entite.id in :idEntite and E.categorie = 'COMMERCIAL' " +
            "and (E.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
            "AND (  E.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date)  is null )" +
            "AND ((:numBillet is null or :numBillet = '') or E.numBillet like CONCAT('%', :numBillet, '%'))" +
            "and (:classeR is null or :classeR = '' or lower(E.classeReservation) like lower(concat('%',:classeR,'%') ))" +
            "and (:origine is null or :origine = '' or lower(E.escaleDepart) like lower(concat('%',:origine,'%') ) )" +
            "AND (E.montantBrut >= :montantDebut or :montantDebut = 0.0  )" +
            "AND (E.montantBrut <= :montantFin or :montantFin = 0.0) " +
            "and (:destination is null or :destination = '' or lower(E.escaleArrivee) like lower(concat('%',:destination,'%') ))"+
            "and ((:statutVenteEnum) is null or E.statutVente in (:statutVenteEnum)) " +
            "AND (LOWER(E.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')) or :agence like '') " +
            "and ((:portfeuille) is null  or lower(ep.nom) in (:portfeuille)) " +
            "AND (LOWER(E.signature) = LOWER(:signature) or :signature = '') " +
            "and ((:representation) is null  or lower(epp.nom) in (:representation)) " +
            "AND (" +
            "    (:motif IS NULL OR :motif = '') " +
            "or ( :motif <> ''  " +
            "AND (lower(E.motif_recette) LIKE lower(concat('%', :motif, '%'))" +
            "or  lower(E.motif_vente) LIKE lower(concat('%', :motif, '%')))" +
            ")" +
            ")"+
            "order by E.numBillet desc"
    )
    List<ExportEmissionView> getEmissionExportVenteByEntiteList(LocalDateTime dateTimeDebut , LocalDateTime dateTimeFin, String origine, String destination, String classeR, Double montantDebut, Double montantFin,String agence, List<String> portfeuille, List<String> representation,List<Long> idEntite,List<StatutVenteEnum> statutVenteEnum,String numBillet,String signature,String motif);

    @Query("select E from ExportEmissionView E LEFT JOIN E.entite ee " +
            "LEFT JOIN ee.parent ep " +
            "LEFT JOIN ep.parent epp " +
            "where E.classeReservation <> ' ' and E.entite is not null and" +
            " (E.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
            "AND (  E.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date)  is null )" +
            "and ( :classeR is null or :classeR like '' or lower(E.classeReservation) like lower(concat('%',:classeR,'%') )) " +
            "and ( :origine is null or :origine like '' or lower(E.escaleDepart) like lower(concat('%',:origine,'%') ) ) " +
            "AND ( :montantDebut is null or :montantDebut = 0.0  or E.montantBrut >= :montantDebut  ) " +
            "AND ( :montantFin is null or :montantFin = 0.0 or E.montantBrut <= :montantFin ) " +
            "and ( :destination is null or :destination like '' or lower(E.escaleArrivee) like lower(concat('%',:destination,'%') )) "+
            "AND (( :numBillet is null or :numBillet like '') or E.numBillet like CONCAT('%', :numBillet, '%')) "+
            "and ((:statutVenteEnum) is null or E.statutVente in (:statutVenteEnum)) " +
            "AND (LOWER(E.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')) or :agence like '') " +
            "and ((:portfeuille) is null  or lower(ep.nom) in (:portfeuille)) " +
            "AND (LOWER(E.signature) = LOWER(:signature) or :signature = '') " +
            "and ((:representation) is null  or lower(epp.nom) in (:representation)) " +
            "AND (" +
            "    (:motif IS NULL OR :motif = '') " +
            "or ( :motif <> ''  " +
            "AND (lower(E.motif_recette) LIKE lower(concat('%', :motif, '%'))" +
            "or  lower(E.motif_vente) LIKE lower(concat('%', :motif, '%')))" +
            ")" +
            ")"+
            "order by E.numBillet desc "
    )
    List<ExportEmissionView> getEmissionExportVentelist(LocalDateTime dateTimeDebut , LocalDateTime dateTimeFin, String origine,
                                                        String destination, String classeR, Double montantDebut, Double montantFin,String agence,
                                                        List<String> portfeuille, List<String> representation,List<StatutVenteEnum> statutVenteEnum,
                                                        String numBillet,String signature,String motif);

}
