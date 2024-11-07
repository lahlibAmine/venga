package com.ram.venga.repos;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Entite;
import com.ram.venga.domain.Vente;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.projection.CollaborateurSearchProjection;
import com.ram.venga.projection.CollaborateurView;
import com.ram.venga.projection.CollaborateursChiffreAffaireReportProjection;
import com.ram.venga.projection.CollaborateursPointsReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {

    boolean existsByCodeIgnoreCase(String code);

    Collaborateur findByEmail(String email);

    List<Collaborateur> getCollaborateursByCategorie(CategorieCollaborateurEnum categorie);


    @Query(value = "select C from Collaborateur  C " +
            "LEFT JOIN C.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where  " +
            " C.categorie = 'COMMERCIAL' " +
            "and ( lower(C.code)  LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( C.nom) LIKE LOWER(CONCAT('%', :keyWord, '%')) or lower( C.signature) like LOWER(CONCAT('%', :keyWord, '%')) or TO_CHAR(C.soldePoint, 'FM999999999999999999999999') LIKE LOWER(CONCAT('%', :keyWord, '%')) or " +
            " lower(E.telephone) LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( E.nom ) LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( E.adresse ) LIKE LOWER(CONCAT('%', :keyWord, '%') )" +
            " or lower( E.email) LIKE LOWER(CONCAT('%', :keyWord, '%') )  or lower(E.fax) LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( C.email) LIKE LOWER(CONCAT('%', :keyWord, '%') )" +
            " OR :keyWord = '') " +
            "AND ((:portfeuille = 'vide' and EP is null ) or(LOWER(EP.nom) = LOWER(:portfeuille) or :portfeuille = '')) " +
            "AND ((:representation = 'vide' and EPP is null ) or(LOWER(EPP.nom) = LOWER(:representation) or :representation = '')) " +
            " order by C.soldePoint desc ")
    Page<Collaborateur> findAllCollaborateurPointsAdmin(Pageable pageable,String keyWord,String portfeuille,String representation);

    @Query(value = "select C.signature as signature, C.nom as nom, C.prenom as prenom, C.email as email, C.soldePoint as numberDePoints, E.nom as agence, E.code as codeIATA, EP.nom as portefeuille, EPP.nom as representation " +
            "from Collaborateur C " +
            "LEFT JOIN C.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where " +
            " C.categorie = 'COMMERCIAL' " +
            "and (" +
            "   lower(C.code) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.nom) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.signature) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or TO_CHAR(C.soldePoint, 'FM999999999999999999999999') LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.telephone) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.nom) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.adresse) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.email) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.fax) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.email) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or :keyWord = '' " +
            ") " +
            "and ((:portefeuille = 'vide' and EP is null ) or(:portefeuille is null or EP.nom = :portefeuille)) " +
            "and ((:representation = 'vide' and EPP is null ) or(:representation is null or EPP.nom = :representation)) " +
            "order by C.soldePoint desc")
    List<CollaborateursPointsReportProjection> findAllCollaborateurPointsListForAdmins(String keyWord, String  representation, String  portefeuille);


    @Query("select C from Collaborateur as C where C.entite.id in :idEntite and C.categorie = 'COMMERCIAL' order by C.dateCreated desc")
    List<Collaborateur> findByProfilRattacherWithEntite(List<Long> idEntite);

    @Query("SELECT C FROM Collaborateur AS C WHERE C.entite.id IN :idEntite AND C.categorie = 'COMMERCIAL' AND (:agencePattern is null or :agencePattern = '') or ((:agencePattern <> '' or :agencePattern is not null )  and lower(C.entite.nom) LIKE lower(:agencePattern)) ORDER BY C.dateCreated DESC")
    List<Collaborateur> findByProfilRattacherWithEntiteFilter(List<Long> idEntite, String agencePattern);

    @Query("SELECT C FROM Collaborateur C WHERE C.categorie = 'COMMERCIAL' and (:agencePattern is null or :agencePattern = '') or ((:agencePattern <> '' or :agencePattern is not null )  and lower(C.entite.nom) LIKE lower(:agencePattern) )  ORDER BY C.dateCreated DESC")
    List<Collaborateur> findAllByEntiteNomOrNomIsNull(@Param("agencePattern") String agencePattern);

    @Query("select C from Collaborateur C where C.categorie = 'COMMERCIAL'")
    List<Collaborateur> findAllByAgent();
    @Query("select COALESCE(SUM(C.soldePoint), 0) from Collaborateur C where C.categorie = 'COMMERCIAL'")
    Double sumPointAdmin();
    @Query("select COALESCE(SUM(C.chiffreAffaire), 0) from Collaborateur C where C.categorie = 'COMMERCIAL'")
    Double sumChiffreAffaireAdmin();

    @Query("select COALESCE(SUM(C.soldePoint), 0) from Collaborateur as C where C.entite.id in :idEntite and C.categorie = 'COMMERCIAL'  and C.utilisateur.active = true")
   Double  sumPointRattacher(List<Long> idEntite);

    @Query("select COALESCE(SUM(C.chiffreAffaire), 0) from Collaborateur as C where C.entite.id IN :idEntite and C.categorie = 'COMMERCIAL' and C.utilisateur.active = true")
    Double sumChiffreAffaireRattacher(List<Long> idEntite);

    @Query("select C from Collaborateur as C where C.entite.id in :idEntite  and C.utilisateur.active = true and C.categorie = 'COMMERCIAL' and C.chiffreAffaire is not null order by C.chiffreAffaire desc ")
    List<Collaborateur> findByProfilRattacherWithEntiteLimit(List<Long> idEntite,Pageable pageable);
    @Query("select C from Collaborateur C where C.categorie = 'COMMERCIAL' and C.chiffreAffaire is not null order by C.chiffreAffaire desc")
    List<Collaborateur> findAllByAgentLimit(Pageable pageable);

    @Query("select C from Collaborateur C where C.categorie = 'COMMERCIAL' order by C.dateCreated desc")
    List<Collaborateur> findAllWithCollaborateur();

    @Query("select C from Collaborateur C where C.signature = :signature and C.categorie = 'COMMERCIAL'")
    Collaborateur findBySignature(String signature);
    @Query("SELECT C.signature FROM Collaborateur C where C.signature is not null and C.signature <> '' order by C.signature desc")
    List<String> findMaxSignatureNumber();

    @Query("select C from Collaborateur C where C.entite.id in :idEntite and C.signature = :signature and C.categorie = 'COMMERCIAL'")
    Collaborateur findBySignatureWithEntite(String signature, List<Long> idEntite);

    @Query("select C from Collaborateur C where C.categorie = 'COMMERCIAL' and (:keyword IS NULL OR :keyword = '') OR " +
          "(:keyword <> '' AND "+
            "(LOWER(C.entite.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) or " +
            "LOWER(C.signature) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            ")")
    Page<Collaborateur> findAllByAgentPage(String keyword,Pageable pageable);


    @Query("SELECT C FROM Collaborateur C " +
            "LEFT JOIN C.entite e " +
            "LEFT JOIN e.parent ep " +
            "LEFT JOIN ep.parent epp" +
            " inner join Utilisateur u on u.collaborateur = C " +
            "WHERE C.categorie = 'COMMERCIAL' " +  // Only includes 'COMMERCIAL' category
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(C.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.signature) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(ep.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(epp.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY C.dateCreated DESC")
    Page<Collaborateur> findAllByKeyword(String keyword, Pageable pageable);

    // get Max Number as int Code Collaborateur
    @Query(value = "SELECT MAX(C.code) FROM Collaborateur C WHERE C.code IS NOT NULL")
    String getMaxCodeCollaborateur();

    @Query("SELECT C " +
            "FROM Collaborateur C " +
            "LEFT JOIN C.entite E " +
            "inner join Utilisateur u on C.id = u.collaborateur.id " +
            "WHERE C.categorie <> 'COMMERCIAL' " +
            "AND (" +
            "(:keyword IS NULL OR :keyword = '') " +
            "OR " +
            "(:keyword <> '' AND (" +
            "LOWER(C.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(E.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "))" +
            ") " +
            "ORDER BY C.dateCreated DESC")
    Page<Collaborateur> findAllProfilesPageable(String keyword,Pageable pageable);
    @Query("select distinct c from Collaborateur c left join Opperation o on o.signature = c.signature left join RecetteBrute rb on rb.id = o.recetteBrute.id " +
            "LEFT JOIN c.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where  c.categorie = 'COMMERCIAL' and E.id in :idEntite and c.utilisateur.active = true and ((LOWER(E.nom) like LOWER(concat('%', :agencePattern,'%')) or :agencePattern is null or :agencePattern = '' )" +
            " and (LOWER(c.nom) like LOWER(concat('%', :agentPattern,'%')) or LOWER(c.signature) like LOWER(concat('%', :agentPattern,'%')) or :agentPattern is null or :agentPattern = ''))  and (cast(:dateTimeDebut as date) IS NULL OR rb.dateTransport >= :dateTimeDebut ) AND (cast(:dateTimeFin as date) IS NULL OR rb.dateTransport <= :dateTimeFin) " +
            "AND ((:portfeuille = 'vide' and EP is null ) or(LOWER(EP.nom) = LOWER(:portfeuille) or :portfeuille = ''))" +
            " AND ((:representation = 'vide' and EPP is null ) or(LOWER(EPP.nom) = LOWER(:representation) or :representation = ''))" +
            " order by c.chiffreAffaire desc")
    Page<Collaborateur> findAllByAgentAndAgenceWithEntite(String agentPattern, List<Long> idEntite, String agencePattern, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, String portfeuille, String representation, Pageable pageable);

    @Query("select distinct c from Collaborateur c left join Opperation o on o.signature = c.signature left join RecetteBrute rb on rb.id = o.recetteBrute.id " +
            "LEFT JOIN c.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            " where  c.categorie = 'COMMERCIAL' and ((LOWER(E.nom) like LOWER(concat('%', :agencePattern,'%')) or :agencePattern is null or :agencePattern = '' )" +
            " and (LOWER(c.nom) like LOWER(concat('%', :agentPattern,'%')) or LOWER(c.signature) like LOWER(concat('%', :agentPattern,'%')) or :agentPattern is null or :agentPattern = ''))  and (cast(:dateTimeDebut as date) IS NULL OR rb.dateTransport >= :dateTimeDebut ) AND (cast(:dateTimeFin as date) IS NULL OR rb.dateTransport <= :dateTimeFin) " +
            "AND ((:portfeuille = 'vide' and EP is null ) or (LOWER(EP.nom) = LOWER(:portfeuille) or :portfeuille = ''))" +
            "AND ((:representation = 'vide' and EPP is null ) or(LOWER(EPP.nom) = LOWER(:representation) or :representation = ''))" +
            " order by c.chiffreAffaire desc")
    Page<Collaborateur> findAllByAgentAndAgence(String agentPattern,  String agencePattern,LocalDateTime dateTimeDebut,LocalDateTime dateTimeFin, String portfeuille, String representation,Pageable pageable);

    @Query("select E.code as codeIATA , E.nom as agence , c.nom as nom , c.prenom as prenom , c.signature as signature " +
            " ,COALESCE(SUM(rb.montantBrut), 0) as chiffreAffaires , E.adresse as adressAgence ,E.email as emailAgences , c.email as emailAgent , " +
            " E.telephone as teleAgence , c.mobile as mobileAgent , EP.nom as portefeuille , EPP.nom as representation   from Collaborateur c  left join Opperation o on o.signature = c.signature left join RecetteBrute rb on rb.id = o.recetteBrute.id " +
            "LEFT JOIN c.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where  c.categorie = 'COMMERCIAL' and  c.utilisateur.active = true and  c.entite.id in :idEntite and  ((LOWER(c.entite.nom) like LOWER(concat('%', :agencePattern,'%')) or :agencePattern is null or :agencePattern = '' )" +
            " and (LOWER(c.nom) like LOWER(concat('%', :agentPattern,'%')) or LOWER(c.signature) like LOWER(concat('%', :agentPattern,'%')) or :agentPattern is null or :agentPattern = ''))  and (cast(:dateTimeDebut as date) IS NULL OR rb.dateTransport >= :dateTimeDebut ) AND (cast(:dateTimeFin as date) IS NULL OR rb.dateTransport <= :dateTimeFin) " +
            "AND ((:portfeuille = 'vide' and EP is null ) or (LOWER(EP.nom) = LOWER(:portfeuille) or :portfeuille = ''))" +
            " AND ((:representation = 'vide' and EPP is null ) or(LOWER(EPP.nom) = LOWER(:representation) or :representation = ''))" +
            " group by E.code,E.nom,E.adresse, E.email, E.telephone , c.nom ,c.prenom , c.signature,c.email,c.mobile, EP.nom, EPP.nom order by  COALESCE(SUM(rb.montantBrut), 0) ")
    List<CollaborateursChiffreAffaireReportProjection> findAllByAgentAndAgenceWithEntiteList(String agentPattern, List<Long> idEntite, String agencePattern, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, String portfeuille, String representation);

    @Query("select E.code as codeIATA , E.nom as agence , c.nom as nom , c.prenom as prenom , c.signature as signature" +
            " ,COALESCE(SUM(rb.montantBrut), 0) as chiffreAffaires , E.adresse as adressAgence ,E.email as emailAgences , c.email as emailAgent ," +
            " E.telephone as teleAgence , c.mobile as mobileAgent , EP.nom as portefeuille , EPP.nom as representation from Collaborateur c  left join Opperation o on o.signature = c.signature left join RecetteBrute rb on rb = o.recetteBrute "+
            " LEFT JOIN c.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where " +
            "  c.categorie = 'COMMERCIAL' and ((LOWER(c.entite.nom) like LOWER(concat('%', :agencePattern,'%')) or :agencePattern is null or :agencePattern = '' )" +
            " and (LOWER(c.nom) like LOWER(concat('%', :agentPattern,'%')) or LOWER(c.signature) like LOWER(concat('%', :agentPattern,'%')) or :agentPattern is null or :agentPattern = ''))  and (cast(:dateTimeDebut as date) IS NULL OR rb.dateTransport >= :dateTimeDebut ) AND (cast(:dateTimeFin as date) IS NULL OR rb.dateTransport <= :dateTimeFin) " +
            "AND ((:portfeuille = 'vide' and EP is null ) or(LOWER(EP.nom) = LOWER(:portfeuille) or :portfeuille = ''))" +
            "AND ((:representation = 'vide' and EPP is null ) or(LOWER(EPP.nom) = LOWER(:representation) or :representation = ''))" +
            "group by E.code,E.nom,E.adresse, E.email, E.telephone , c.nom ,c.prenom , c.signature,c.email,c.mobile, EP.nom, EPP.nom order by COALESCE(SUM(rb.montantBrut), 0) desc ")
    List<CollaborateursChiffreAffaireReportProjection> findAllByAgentAndAgenceList(String agentPattern,  String agencePattern,LocalDateTime dateTimeDebut,LocalDateTime dateTimeFin, String portfeuille, String representation);
    @Query("select C from Collaborateur C where C.entite = :entite")
    List<Collaborateur> findAllByEntite(Entite entite);

    @Query("SELECT C " +
            "FROM Collaborateur C  " +
            "WHERE C.categorie = 'COMMERCIAL' " +
            "AND C.utilisateur.active = true " +
            "AND (" +
            "(:keyword IS NULL OR :keyword = '') " +
            "OR " +
            "(:keyword <> '' AND (" +
            "LOWER(C.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.signature) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.entite.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "))" +
            ") " +
            "AND C.entite.parent.id = :idPortefeuille "+
            "ORDER BY C.dateCreated DESC")
    Page<Collaborateur> findAgentsByPorteuille(Long idPortefeuille,String keyword, Pageable pageable);

    @Query("SELECT C " +
            "FROM Collaborateur C " +
            "WHERE C.categorie = 'COMMERCIAL' " +
            "AND C.utilisateur.active = true " +
            "AND (" +
            "(:keyword IS NULL OR :keyword = '') " +
            "OR " +
            "(:keyword <> '' AND (" +
            "LOWER(C.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.signature) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(C.entite.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "))" +
            ") " +
            "AND C.entite.parent.parent.id in :ids "+
            "ORDER BY C.dateCreated DESC")
    Page<Collaborateur> findAgentsByListOfRepresentationIds(List<Long> ids, String keyword, Pageable pageable);
    @Query("select c from Collaborateur c where c.categorie <> 'COMMERCIAL'")
    List<Collaborateur> getCollaborateursNotCommercial();

    @Query("select c.utilisateur.email from Collaborateur c where c.categorie = 'TECHNIQUE' ")
    List<String> findAllByCategorie();
    @Query(value = "select C from Collaborateur  C " +
            "LEFT JOIN C.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where(E.id in :idEntites and C.utilisateur.active = true) and " +
            " C.categorie = 'COMMERCIAL' " +
            "and ( lower(C.code)  LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( C.nom) LIKE LOWER(CONCAT('%', :keyWord, '%')) or lower( C.signature) like LOWER(CONCAT('%', :keyWord, '%')) or TO_CHAR(C.soldePoint, 'FM999999999999999999999999') LIKE LOWER(CONCAT('%', :keyWord, '%')) or " +
            " lower(E.telephone) LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( E.nom ) LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( E.adresse ) LIKE LOWER(CONCAT('%', :keyWord, '%') )" +
            " or lower( E.email) LIKE LOWER(CONCAT('%', :keyWord, '%') )  or lower(E.fax) LIKE LOWER(CONCAT('%', :keyWord, '%') ) or lower( C.email) LIKE LOWER(CONCAT('%', :keyWord, '%') )" +
            " OR :keyWord = '') " +
            "AND ((:portfeuille = 'vide' and EP is null ) or(LOWER(EP.nom) = LOWER(:portfeuille) or :portfeuille = '')) " +
            "AND ((:representation = 'vide' and EPP is null ) or(LOWER(EPP.nom) = LOWER(:representation) or :representation = '')) " +
            " order by C.soldePoint desc ")
    Page<Collaborateur> findAllCollaborateurPointsRattacher(Pageable sortedPageable, String keyWord, List<Long> idEntites, String portfeuille, String representation);
    @Query("SELECT C.nom as nom , C.prenom as prenom , C.email as email " +
            ", C.telephone as telephone , e.nom as agenceNom , e.code as agenceCode" +
            ", ep.nom as portfeuilleNom , epp.nom as representationNom ," +
            "C.dateCreated as dateCreation , C.utilisateur.active as statut , C.signature as  signature FROM Collaborateur C " +
            "LEFT JOIN C.entite e " +
            "LEFT JOIN e.parent ep " +
            "LEFT JOIN ep.parent epp " +
            "WHERE C.categorie = 'COMMERCIAL' " +  // Only includes 'COMMERCIAL' category
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(C.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.signature) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(ep.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(epp.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY C.dateCreated DESC")
    List<CollaborateurSearchProjection> findAllByKeywordList(String keyword);
    @Query("SELECT C.nom as nom , C.prenom as prenom , C.email as email " +
            ", C.telephone as telephone , e.nom as agenceNom , e.code as agenceCode" +
            ", ep.nom as portfeuilleNom , epp.nom as representationNom ," +
            "C.dateCreated as dateCreation , C.utilisateur.active as statut , C.signature as  signature FROM Collaborateur C " +
            "LEFT JOIN C.entite e " +
            "LEFT JOIN e.parent ep " +
            "LEFT JOIN ep.parent epp " +
            "WHERE C.categorie = 'COMMERCIAL'" +
            "And e.parent.id in :idsPortfeuille" +
            " AND C.utilisateur.active = true " +  // Only includes 'COMMERCIAL' category
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(C.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(C.signature) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(ep.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(epp.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY C.dateCreated DESC")
    List<CollaborateurSearchProjection> findAllByKeywordListFilterPotfeuille(String keyword, List<Long> idsPortfeuille);
    @Query(value = "select C.signature as signature, C.nom as nom, C.prenom as prenom, C.email as email, C.soldePoint as numberDePoints, E.nom as agence, E.code as codeIATA, EP.nom as portefeuille, EPP.nom as representation " +
            "from Collaborateur C " +
            "LEFT JOIN C.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where" +
            " EPP.id in :representationIds and " +
            " C.categorie = 'COMMERCIAL' and C.utilisateur.active = true " +
            "and (" +
            "   lower(C.code) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.nom) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.signature) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or TO_CHAR(C.soldePoint, 'FM999999999999999999999999') LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.telephone) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.nom) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.adresse) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.email) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.fax) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.email) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or :keyWord = '' " +
            ") " +
            "and ((:portefeuille = 'vide' and EP is null ) or(:portefeuille is null or EP.nom = :portefeuille)) " +
            "and ((:representation = 'vide' and EPP is null ) or(:representation is null or EPP.nom = :representation)) " +
            "order by C.soldePoint desc")
    List<CollaborateursPointsReportProjection> findAllCollaborateurPointsListForRAttORCon(String keyWord, String representation, String portefeuille, List<Long> representationIds);
    @Query(value = "select C.signature as signature, C.nom as nom, C.prenom as prenom, C.email as email, C.soldePoint as numberDePoints, E.nom as agence, E.code as codeIATA, EP.nom as portefeuille, EPP.nom as representation " +
            "from Collaborateur C " +
            "LEFT JOIN C.entite E " +
            "LEFT JOIN E.parent EP " +
            "LEFT JOIN EP.parent EPP " +
            "where" +
            " EP.id = :portfeuilleId and C.utilisateur.active = true and " +
            " C.categorie = 'COMMERCIAL' " +
            "and (" +
            "   lower(C.code) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.nom) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.signature) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or TO_CHAR(C.soldePoint, 'FM999999999999999999999999') LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.telephone) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.nom) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.adresse) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.email) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(E.fax) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or lower(C.email) LIKE lower(CONCAT('%', :keyWord, '%')) " +
            "   or :keyWord = '' " +
            ") " +
            "and ((:portefeuille = 'vide' and EP is null ) or (:portefeuille is null or EP.nom = :portefeuille)) " +
            "and ((:representation = 'vide' and EPP is null ) or (:representation is null or EPP.nom = :representation)) " +
            "order by C.soldePoint desc")
    List<CollaborateursPointsReportProjection> findAllCollaborateurPointsListForRAttByPortfeuilleId(String keyWord, String representation, String portefeuille,Long portfeuilleId);
    @Query("select COALESCE(SUM(C.soldePoint), 0) from Collaborateur C where C.categorie = 'COMMERCIAL' and C.signature = :signature")
    Double getSumCredit(String signature);
    @Query("select COALESCE(SUM(o.debit), 0) from Collaborateur C inner join Opperation o on o.signature = C.signature where C.categorie = 'COMMERCIAL' and C.signature = :signature")
    Double getSumDebit(String signature);
}
