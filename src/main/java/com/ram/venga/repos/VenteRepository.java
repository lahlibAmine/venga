package com.ram.venga.repos;

import com.ram.venga.domain.BonCommande;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Vente;
import com.ram.venga.model.VenteDTO;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import com.ram.venga.projection.CheckSignatureVenteRepost;
import com.ram.venga.projection.ListEmissionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.*;
import java.util.*;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {
    @Query("select distinct v from Vente v inner join Opperation o on o.signature = v.signatureAgent where v.isArchived = false and v.collaborateur.id = :idUser and o.debit <> 0")
    List<Vente> findByCollaborateurId(Long idUser);

    @Query("select V from Vente V where V.collaborateur.id = :idUser and V.isArchived = false AND V.dateTransport >= :startDate AND V.dateTransport <= :endDate ")
    List<Vente> findByDateTransportAfterAndDateTransport(Long idUser, LocalDateTime startDate, LocalDateTime endDate);


    @Query("select distinct  new com.ram.venga.model.VenteDTO( " +
            " v.numBillet,v.pnr,v.nbrCoupon,v.dateEmission" +
            " ,v.signatureAgent,  v.codeIATA,  e.nom," +
            " SUM(CASE WHEN o.debit IS NULL THEN 0 ELSE o.debit END) , v.venteIntgre, " +
            "v.venteRapproche, v.collaborateur.nom " +
            ", v.collaborateur.email ,v.collaborateur.mobile,v.statutVente ,e.nom,epp2.nom,ep2.nom,v.dateCreated) " +
            "from Vente v " +
            "left join RecetteBrute rb on rb.numBillet = v.numBillet " +
            "left join Opperation o on o.recetteBrute = rb " +
            "left join Entite e on e.code = v.codeIATA " +
            "left join Entite ep2 on ep2 = e.parent " +
            "left join Entite epp2 on epp2 = e.parent.parent " +
            "left join Entite e1 on e1 = v.collaborateur.entite " +
            "left join Entite ep1 on ep1 = v.collaborateur.entite.parent" +
            " left join Entite epp1 on epp1 = v.collaborateur.entite.parent.parent" +
            " where ( rb is null or rb.classeReservation <> ' ' ) and v.collaborateur.categorie = 'COMMERCIAL' " +
            "and v.isArchived = :isArchived " +
            "and (v.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
            "AND (  v.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date)  is null )" +
            "and (:classeR is null or :classeR = '' or lower(rb.classeReservation) like lower(concat('%', :classeR, '%'))) " +
            "and (:origine is null or :origine = '' or lower(rb.escaleDepart) like lower(concat('%', :origine, '%'))) " +
            "and (:montantDebut = 0.0 or rb.montantBrut >= :montantDebut) " +
            "and (:montantFin = 0.0 or rb.montantBrut <= :montantFin) " +
            "and (:destination is null or :destination = '' or lower(rb.escaleArrivee) like lower(concat('%', :destination, '%'))) " +
            "and (:numBillet is null or :numBillet = '' or v.numBillet like concat('%', :numBillet, '%')) " +
            "and ((:statutVenteEnum) is null or v.statutVente in (:statutVenteEnum)) " +
            "and (:agence is null or :agence = '' or lower(e1.nom) like lower(concat('%', :agence, '%'))) " +
            "and ((:portfeuille) is null  or lower(ep1.nom) in (:portfeuille)) " +
            "and (:signature is null or :signature = '' or lower(v.collaborateur.signature) = lower(:signature)) " +
            "and ((:representation) is null  or lower(epp1.nom) in (:representation)) " +
            "and (" +
            "(" +
            "    (:motifs is null or :motifs = '' or " +
            "     (lower(rb.motif) in :motifs or lower(v.motif) in :motifs)" +
            "    )" +
            ")" +
            ")group by  v.numBillet,v.pnr,v.nbrCoupon,v.dateEmission" +
            " ,v.signatureAgent,  v.codeIATA," +
            "  v.venteIntgre, " +
            "v.venteRapproche, v.collaborateur.nom " +
            ", v.collaborateur.email ,v.collaborateur.mobile,v.statutVente ,e.nom,epp2.nom,ep2.nom,e.nom,v.dateCreated order by v.dateCreated desc"+
            "")
    Page<VenteDTO> getEmissionVente(
            @Param("dateTimeDebut") LocalDateTime dateTimeDebut,
            @Param("dateTimeFin") LocalDateTime dateTimeFin,
            @Param("agence") String agence,
            @Param("portfeuille") List<String> portfeuille,
            @Param("representation") List<String> representation,
            @Param("statutVenteEnum") List<StatutVenteEnum> statutVenteEnum,
            @Param("numBillet") String numBillet,
            @Param("classeR") String classeR,
            @Param("origine") String origine,
            @Param("destination") String destination,
            @Param("montantDebut") Double montantDebut,
            @Param("montantFin") Double montantFin,
            @Param("signature") String signature,
            @Param("isArchived") Boolean isArchived
            ,List<String> motifs,
            Pageable pageable
    );


    @Query("select V from Vente as V where (V.dateTransport >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
            " and V.isArchived = false AND (  V.dateTransport <= :dateTimeFin or CAST(:dateTimeFin AS date)  is null ) and V.collaborateur in :collaborateur and V.collaborateur.categorie = 'COMMERCIAL' and " +
            " ((:originePattern is null or :originePattern = '') or ((:originePattern <> '' or :originePattern is not null) " +
            "and lower(V.origineEmission.nom) LIKE lower(:originePattern))) order by V.dateCreated")
    List<Vente> findByChiffreAffaireWithEntite(List<Collaborateur> collaborateur, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin,String originePattern);

    @Query("select distinct  new com.ram.venga.model.VenteDTO( " +
            " v.numBillet,v.pnr,v.nbrCoupon,v.dateEmission" +
            " ,v.signatureAgent,  v.codeIATA,  e.nom," +
            " SUM(CASE WHEN o.debit IS NULL THEN 0 ELSE o.debit END) , v.venteIntgre, " +
            "v.venteRapproche, v.collaborateur.nom " +
            ", v.collaborateur.email ,v.collaborateur.mobile,v.statutVente ,e.nom,epp2.nom,ep2.nom,v.dateCreated) " +
            "from Vente v " +
            "left join RecetteBrute rb on rb.numBillet = v.numBillet " +
            "left join Opperation o on o.recetteBrute = rb " +
            "left join Entite e on e.code = v.codeIATA " +
            "left join Entite ep2 on ep2 = e.parent " +
            "left join Entite epp2 on epp2 = e.parent.parent " +
            "left join Entite e1 on e1 = v.collaborateur.entite " +
            "left join Entite ep1 on ep1 = v.collaborateur.entite.parent" +
            " left join Entite epp1 on epp1 = v.collaborateur.entite.parent.parent" +
            " where (rb.classeReservation <> ' ' or rb is null) and v.isArchived = false and  v.collaborateur.entite.id in :idEntite and v.collaborateur.categorie = 'COMMERCIAL' " +
            "and (v.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
            "AND (  v.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date)  is null )" +
            "and (:classeR is null or :classeR = '' or lower(rb.classeReservation) like lower(concat('%', :classeR, '%'))) " +
            "and (:origine is null or :origine = '' or lower(rb.escaleDepart) like lower(concat('%', :origine, '%'))) " +
            "and (:montantDebut = 0.0 or rb.montantBrut >= :montantDebut) " +
            "and (:montantFin = 0.0 or rb.montantBrut <= :montantFin) " +
            "and (:destination is null or :destination = '' or lower(rb.escaleArrivee) like lower(concat('%', :destination, '%'))) " +
            "and (:numBillet is null or :numBillet = '' or v.numBillet like concat('%', :numBillet, '%')) " +
            "and ((:statutVenteEnum) is null or v.statutVente in (:statutVenteEnum)) " +
            "and (:agence is null or :agence = '' or lower(e1.nom) like lower(concat('%', :agence, '%'))) " +
            "and ((:portfeuille) is null  or lower(ep1.nom) in (:portfeuille)) " +
            "and (:signature is null or :signature = '' or lower(v.collaborateur.signature) = lower(:signature)) " +
            "and ((:representation) is null  or lower(epp1.nom) in (:representation)) " +
            "and (:motif1 is null or :motif1 = '' or " +
            "(lower(rb.motif) like lower(concat('%', :motif1, '%')) " +
            "or lower(v.motif) like lower(concat('%', :motif1, '%')))) " +
            "or (:motif2 is null or :motif2 = '' or " +
            "(lower(rb.motif) like lower(concat('%', :motif2, '%')) " +
            "or lower(v.motif) like lower(concat('%', :motif2, '%')))) " +
            "or (:motif3 is null or :motif3 = '' or " +
            "(lower(rb.motif) like lower(concat('%', :motif3, '%')) " +
            "or lower(v.motif) like lower(concat('%', :motif3, '%')))) " +
            "or (:motif4 is null or :motif4 = '' or " +
            "(lower(rb.motif) like lower(concat('%', :motif4, '%')) " +
            "or lower(v.motif) like lower(concat('%', :motif4, '%')))) "+
            " group by  v.numBillet,v.pnr,v.nbrCoupon,v.dateEmission" +
            " ,v.signatureAgent,  v.codeIATA," +
            "  v.venteIntgre, " +
            "v.venteRapproche, v.collaborateur.nom " +
            ", v.collaborateur.email ,v.collaborateur.mobile,v.statutVente ,v.collaborateur.entite.nom, e.nom,epp2.nom,ep2.nom,e.nom,v.dateCreated order by v.dateCreated desc"
    )
    Page<VenteDTO> getEmissionVenteByEntite(LocalDateTime dateTimeDebut , LocalDateTime dateTimeFin, String agence, List<String> portfeuille, List<String> representation, List<Long> idEntite, List<StatutVenteEnum> statutVenteEnum,String numBillet,String classeR,String origine,String destination, Double montantDebut, Double montantFin,String signature,String motif1,String motif2,String motif3,String motif4,Pageable pageable);

    @Query("select distinct V from Vente V inner join JournalRapprochement jr on jr.vente.id = V.id inner join RecetteBrute R on R.id = jr.recette.id  where  V.collaborateur.entite.id in :idEntite and V.isArchived = false and  V.collaborateur.categorie = 'COMMERCIAL' " +
            "and (V.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
            "AND (  V.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date)  is null )" +
            "AND ((:numBillet is null or :numBillet = '') or V.numBillet like CONCAT('%', :numBillet, '%'))" +
            "and (:classeR is null or :classeR = '' or lower(R.classeReservation) like lower(concat('%',:classeR,'%') ))" +
            "and (:origine is null or :origine = '' or lower(R.escaleDepart) like lower(concat('%',:origine,'%') ) )" +
            "AND (R.montantBrut >= :montantDebut or :montantDebut = 0.0  )" +
            "AND (R.montantBrut <= :montantFin or :montantFin = 0.0) " +
            "and (:destination is null or :destination = '' or lower(R.escaleArrivee) like lower(concat('%',:destination,'%') ))"+
            "AND (V.statutVente = :statutVenteEnum or :statutVenteEnum is null)" +
            "AND (LOWER(V.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')) or :agence like '') " +
            "AND (LOWER(V.collaborateur.entite.parent.nom) = LOWER(:portfeuille) or :portfeuille = '') " +
            "AND (LOWER(V.collaborateur.signature) = LOWER(:signature) or :signature = '') " +
            "AND (LOWER(V.collaborateur.entite.parent.parent.nom) = LOWER(:representation) or :representation = '') " +
            "order by V.dateCreated desc"
    )
    List<Vente> getEmissionVenteByEntiteList(LocalDateTime dateTimeDebut , LocalDateTime dateTimeFin, String origine, String destination, String classeR, Double montantDebut, Double montantFin,String agence, String portfeuille, String representation,Long idEntite,StatutVenteEnum statutVenteEnum,String numBillet,String signature);
    @Query("select distinct V from Vente V  inner join JournalRapprochement jr on jr.vente.id = V.id inner join RecetteBrute R on R.id = jr.recette.id  where (V.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
            " and V.isArchived = false " +
            "AND (  V.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date)  is null )"+
            "and (:classeR is null or :classeR = '' or lower(R.classeReservation) like lower(concat('%',:classeR,'%') ))" +
            "and (:origine is null or :origine = '' or lower(R.escaleDepart) like lower(concat('%',:origine,'%') ) )" +
            "AND (R.montantBrut >= :montantDebut or :montantDebut = 0.0  )" +
            "AND (R.montantBrut <= :montantFin or :montantFin = 0.0) " +
            "and (:destination is null or :destination = '' or lower(R.escaleArrivee) like lower(concat('%',:destination,'%') ))"+
            "AND ((:numBillet is null or :numBillet = '') or V.numBillet like CONCAT('%', :numBillet, '%'))"+
            "AND (V.statutVente = :statutVenteEnum or :statutVenteEnum is null)" +
            "AND (LOWER(V.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')) or :agence like '') " +
            "AND (LOWER(V.collaborateur.entite.parent.nom) = LOWER(:portfeuille) or :portfeuille = '') " +
            "AND (LOWER(V.collaborateur.signature) = LOWER(:signature) or :signature = '') " +
            "AND (LOWER(V.collaborateur.entite.parent.parent.nom) = LOWER(:representation) or :representation = '') " +
            " order by V.dateCreated desc "
    )
    List<Vente> getEmissionVentelist(LocalDateTime dateTimeDebut , LocalDateTime dateTimeFin, String origine, String destination, String classeR, Double montantDebut, Double montantFin,String agence, String portfeuille, String representation,StatutVenteEnum statutVenteEnum,String numBillet,String signature);

    @Query("select v from Vente v where v.numBillet = :numBillet and v.isArchived = false")
    Vente findByNumBillet(String numBillet);
    @Query("select  V.escaleDepart from Vente V where  V.isArchived = false order by V.dateCreated desc")
    Set<String> findByEscalDepart();
    @Query("select  V.escaleArrivee from Vente V where V.isArchived = false  order by V.dateCreated desc")
    Set<String> findByEscalArriver();

    @Query("select V from Vente V where  V.isArchived = false  and V.venteIntgre = true and ( V.venteRapproche = false or V.venteRapproche is null) and V.origineEmission is not null ")
    List<Vente> findAllByIntegre();

    @Query("SELECT V FROM Vente V " +
            "WHERE  V.isArchived = false and V.venteIntgre = true " +
            "AND V.venteRapproche = true " +
            "AND ((V.collaborateur.entite.id) IN (:idEntite))"+
            "AND (:agentPattern IS NULL or :agentPattern = ''  OR lower(V.collaborateur.nom) like  lower(concat('%', :agentPattern,'%'))) " +
            "AND (:agencePattern IS NULL or :agencePattern = '' OR lower(V.collaborateur.entite.nom )  LIKE lower(CONCAT('%', :agencePattern , '%'))) " )
    Page<Vente> findAllByIntegreAndVenteRapproche(
            @Param("agentPattern") String agentPattern,
            @Param("idEntite") List<Long> idEntite,
            @Param("agencePattern") String agencePattern,
            Pageable pageable
    );

    @Query("select V from Vente V " +
            "where  V.isArchived = false " +
            "AND V.codeIATA in " +
                    "(SELECT e.code " +
                        "FROM Entite e " +
                        "WHERE e.categorie ='AGENCE' " +
                        "AND (:portefeuilleId is null or e.parent.id = :portefeuilleId) " +
                        "AND (:representationId is null or e.parent.parent.id = :representationId) )" +
            " and (V.statutVente = 'Rapproche_En_Instance' or V.statutVente = 'Rapproche_Partiellement_EI') " +
            "and ((:numBillet is null or :numBillet ='') or lower(V.numBillet) LIKE CONCAT('%', :numBillet, '%')) " +
            "and ((:origineEmission is null or :origineEmission = '')  or( V.origineEmission.nom LIKE CONCAT('%', :origineEmission, '%'))) " +
            "and ((:codeIata is null or :codeIata = '' )  or V.codeIATA LIKE CONCAT('%', :codeIata, '%')) " +
            " and ((:signature is null or :signature = '' )  or V.signatureAgent LIKE CONCAT('%', :signature, '%')) " +
            "and V.venteIntgre = true ")
    Page<Vente> findByVenteRapprocher(String origineEmission, String codeIata, String numBillet,String signature,Long representationId, Long portefeuilleId ,  Pageable pageable);

    @Query("select v from Vente v where  v.isArchived = false and v.numBillet = :numBillet and v.venteIntgre = true and v.venteRapproche = false")
    Vente findByNumBilletAndIntegrer(String numBillet);

    @Query("select V from Vente V where  V.isArchived = false and (V.statutVente = 'Rapproche_En_Instance' or V.statutVente = 'Rapproche_Partiellement_EI')" +
            "and ((:numBillet is null or :numBillet ='') or lower(V.numBillet) LIKE CONCAT('%', :numBillet, '%')) " +
            " and ((:origineEmission is null or :origineEmission = '')  or( V.origineEmission.nom LIKE CONCAT('%', :origineEmission, '%')))" +
            " and ((:codeIata is null or :codeIata = '' )  or V.codeIATA LIKE CONCAT('%', :codeIata, '%')) " +
            " and ((:signature is null or :signature = '' )  or V.signatureAgent LIKE CONCAT('%', :signature, '%')) " +
            "and V.venteIntgre = true and V.collaborateur.entite in :idEntite ")
    Page<Vente> findByVenteRapprocherAttacher(String origineEmission,String codeIata,String numBillet,String signature,List<Long> idEntite,Pageable pageable);

    @Query("select v from Vente v where  v.isArchived = false and v.numBillet = :numBillet and v.venteIntgre = true")
    Vente findByNumBilletAndInteger(String numBillet);

    @Query("SELECT V FROM Vente V " +
            "WHERE  V.isArchived = false and V.venteIntgre = true " +
            "AND V.venteRapproche = true " +
            "AND (:agentPattern IS NULL or :agentPattern = '' OR lower(V.collaborateur.nom) like  lower(concat('%', :agentPattern,'%'))) " +
            "AND (:agencePattern IS NULL or :agencePattern = '' OR lower(V.collaborateur.entite.nom )  LIKE lower(CONCAT('%', :agencePattern , '%'))) " )
    Page<Vente> findAllByIntegreAndVenteRapprocheWithoutEntite(
            @Param("agentPattern") String agentPattern,
            @Param("agencePattern") String agencePattern,
            Pageable pageable
    );
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vente v WHERE v.numBillet = :numBillet AND v.isArchived = false")
    boolean existsByNumBillet(String numBillet);
    @Query("select v from Vente v where  v.isArchived = false and v.statutVente = 'Non_Integere' and v.venteIntgre = false")
    List<Vente> findVenteNonInteger();

    @Query("select v from Vente v where  v.isArchived = false and v.numBillet = :numBillet and v.venteIntgre = true and v.venteRapproche = false")
    Vente findByNumBilletAndIntegerAndNonRapprocher(String numBillet);


    @Modifying
    @Transactional
    @Query("UPDATE Vente v SET v.isArchived = true WHERE v.dateEmission < :dateDebut and (v.statutVente = 'Rapproche' or v.statutVente = 'Non_Integere') and v.isArchived = false ")
    void updateArchivedVenteRapprocherDates(LocalDateTime dateDebut);

    List<Vente> findAllByIdInAndNumBilletContains(List<Long> ids , String numBillet);
    @Query("select v.numBillet from Vente v where v.dateEmission < :dateDebut and (v.statutVente = 'Rapproche' or v.statutVente = 'Non_Integere') and v.isArchived = false")
    List<String> getNumBilletRapprocherOrNonIntegerAfterMount(LocalDateTime dateDebut);
    @Query("select v from Vente v where v.signatureAgent = :oldSignature and v.collaborateur is null and Cast(v.statutVente as string ) IN ('Rapproche_Partiellement_EI' , 'Rapproche_En_Instance') ")
    List<Vente> findBySignatureNotInCollaborateur(String oldSignature);

    @Query("select V.codeIATA as codeIata" +
            ", V.origineEmission.nom as origineEmission," +
            "V.numBillet as numBillet ," +
            "V.nbrCoupon as nbrCoupon," +
            "SUM(o.debit) as nbrPoint," +
            " V.signatureAgent as signature ," +
            "V.dateEmission as dateEmission" +
            ", e.parent.parent.nom as representation," +
            "e.parent.nom as portfeuille " +
            " from Vente V left join RecetteBrute  rb on rb.numBillet =V.numBillet" +
            " left join Opperation o on o.recetteBrute = rb" +
            " Inner join Entite e on e.code= V.codeIATA" +
            " where  V.isArchived = false and (V.statutVente = 'Rapproche_En_Instance' or V.statutVente = 'Rapproche_Partiellement_EI')" +
            "and ((:numBillet is null or :numBillet ='') or lower(V.numBillet) LIKE CONCAT('%', :numBillet, '%')) " +
            " and ((:origineEmission is null or :origineEmission = '')  or( V.origineEmission.nom LIKE CONCAT('%', :origineEmission, '%')))" +
            " and ((:codeIata is null or :codeIata = '' )  or V.codeIATA LIKE CONCAT('%', :codeIata, '%')) " +
            " and ((:signature is null or :signature = '' )  or V.signatureAgent LIKE CONCAT('%', :signature, '%')) " +
            "and V.venteIntgre = true and V.collaborateur.entite.parent = :entite group by  V.codeIATA" +
            ", V.origineEmission.nom ," +
            "V.numBillet ," +
            "V.nbrCoupon ," +
            " V.signatureAgent ," +
            "V.dateEmission" +
            ", e.parent.parent.nom ," +
            "e.parent.nom " )
    List<CheckSignatureVenteRepost> findByVenteRapprocherAttacherList(String origineEmission, String codeIata, String numBillet, String signature, Long entite);

    @Query("select V.origineEmission.nom as origineEmission," +
            "V.codeIATA as codeIata ," +
            "V.numBillet as numBillet ," +
            "V.nbrCoupon as nbrCoupon," +
            " SUM(CAST(o.debit AS long)) as nbrPoint," +
            " V.signatureAgent as signature ," +
            "V.dateEmission as dateEmission" +
            ", e.parent.parent.nom as representation," +
            "e.parent.nom as portfeuille" +
            " from Vente V left join RecetteBrute  rb on rb.numBillet =V.numBillet" +
            " left join Opperation o on o.recetteBrute = rb" +
            " Inner join Entite e on e.code= V.codeIATA" +
            " where V.isArchived = false " +
            "AND V.codeIATA in " +
            "(SELECT e.code " +
            "FROM Entite e " +
            "WHERE e.categorie ='AGENCE' " +
            "AND (:portefeuilleId is null or e.parent.id = :portefeuilleId) " +
            "AND (:representationId is null or e.parent.parent.id = :representationId) )" +
            " and (V.statutVente = 'Rapproche_En_Instance' or V.statutVente = 'Rapproche_Partiellement_EI') " +
            "and ((:numBillet is null or :numBillet ='') or lower(V.numBillet) LIKE CONCAT('%', :numBillet, '%')) " +
            "and ((:origineEmission is null or :origineEmission = '')  or( V.origineEmission.nom LIKE CONCAT('%', :origineEmission, '%'))) " +
            "and ((:codeIata is null or :codeIata = '' )  or V.codeIATA LIKE CONCAT('%', :codeIata, '%')) " +
            " and ((:signature is null or :signature = '' )  or V.signatureAgent LIKE CONCAT('%', :signature, '%')) " +
            "and V.venteIntgre = true group by  V.codeIATA" +
            ", V.origineEmission.nom ," +
            "V.numBillet ," +
            "V.nbrCoupon ," +
            " V.signatureAgent ," +
            "V.dateEmission" +
            ", e.parent.parent.nom ," +
            "e.parent.nom" )
    List<CheckSignatureVenteRepost> findByVenteRapprocherList(String origineEmission, String codeIata, String numBillet,String signature,Long representationId, Long portefeuilleId );
    @Query("select distinct v from Vente v inner join RecetteBrute rb on rb.numBillet = v.numBillet where v.signatureAgent = :signature and rb.id = :recetteBrute")
    List<Vente> findBySignatureAndRecette(String signature, Long recetteBrute);

}


