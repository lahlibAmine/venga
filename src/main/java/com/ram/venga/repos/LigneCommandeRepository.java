package com.ram.venga.repos;

import com.ram.venga.domain.LigneCommande;
import com.ram.venga.projection.CommandeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    @Query("SELECT L "+
            "FROM LigneCommande L " +
            "WHERE L.bonCommande.agentCommercial.id = :idUser " +
            "AND (L.bonCommande.dateCreated >= :dateTimeDebut or cast(:dateTimeDebut as date) is null) " +
            "AND (L.bonCommande.dateCreated <= :dateTimeFin or  cast(:dateTimeFin as date) is null) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) >= :pointDebut OR :pointDebut = 0) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) <= :pointFin OR :pointFin = 0)" +
            "AND (L.cadeauxBA.fournisseur.nom = :fournisseur OR :fournisseur is null)" +
            "AND (:cadeau IS NULL OR L.cadeauxBA.categorieCadeau.libelle = :cadeau ) order by L.dateCreated desc ")
    Page<LigneCommande> findByBonCommandeFilter(
            @Param("idUser") Long idUser, @Param("cadeau") String cadeau,
            @Param("dateTimeDebut") LocalDateTime dateTimeDebut,
            @Param("dateTimeFin") LocalDateTime dateTimeFin,
            @Param("pointDebut") Integer pointDebut,
            @Param("pointFin") Integer pointFin, @Param("fournisseur") String fournisseur, Pageable pageable);
    @Query("SELECT L "+
            "FROM LigneCommande L " +
            "WHERE"+
            " (L.bonCommande.dateCreated >= :dateTimeDebut or cast(:dateTimeDebut as date) is null) " +
            "AND (L.bonCommande.dateCreated <= :dateTimeFin or  cast(:dateTimeFin as date) is null) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) >= :pointDebut OR :pointDebut = 0) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) <= :pointFin OR :pointFin = 0)" +
            "AND (LOWER(L.cadeauxBA.fournisseur.nom) LIKE LOWER(CONCAT('%', :fournisseur, '%')) OR :fournisseur = '' )" +
            "AND (LOWER(L.cadeauxBA.categorieCadeau.libelle) LIKE LOWER(CONCAT('%', :cadeau, '%')) OR :cadeau = '' )" +
            "AND ((LOWER(L.bonCommande.agentCommercial.collaborateur.nom) LIKE LOWER(CONCAT('%', :agent, '%'))) OR (LOWER(L.bonCommande.agentCommercial.collaborateur.signature) LIKE LOWER(CONCAT('%', :agent, '%'))) or :agent = '' )" +
            //"AND (LOWER(L.bonCommande.agentCommercial.collaborateur.signature) LIKE LOWER(CONCAT('%', :agent, '%')) OR :agent = '' )" +
            "AND (LOWER(L.bonCommande.agentCommercial.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')) OR :agence = '' ) order by L.dateCreated" )
    Page<LigneCommande> findByBonCommande( String cadeau, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, Integer pointDebut, Integer pointFin, String fournisseur,String agent/*,String signature*/,String agence, Pageable pageable);

    @Query("SELECT L "+
            "FROM LigneCommande L " +
            "WHERE L.bonCommande.agentCommercial.collaborateur.entite.parent.id = :idEntite " +
            "AND (L.bonCommande.dateCreated >= :dateTimeDebut or cast(:dateTimeDebut as date) is null) " +
            "AND (L.bonCommande.dateCreated <= :dateTimeFin or  cast(:dateTimeFin as date) is null) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) >= :pointDebut OR :pointDebut = 0) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) <= :pointFin OR :pointFin = 0) " +
            "AND (:fournisseur = '' OR (LOWER(L.cadeauxBA.fournisseur.nom) LIKE LOWER(CONCAT('%', :fournisseur, '%')))) " +
            "AND (:agent = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.nom) LIKE LOWER(CONCAT('%', :agent, '%')))) " +
            "AND (:agence = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')))) " +
            "AND (:cadeau = '' OR (LOWER(L.cadeauxBA.categorieCadeau.libelle) LIKE LOWER(CONCAT('%', :cadeau, '%')))) " +
            "order by L.dateCreated desc ")
    Page<LigneCommande> getLigneCommandeRelatedToAuthenticatedRatache( String cadeau, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, Integer pointDebut, Integer pointFin, String fournisseur,String agent,String agence, Long idEntite, Pageable pageable);


    @Query("SELECT L "+
            "FROM LigneCommande L " +
            "WHERE L.bonCommande.agentCommercial.collaborateur.entite.parent.parent.id in :representationsIds and"+
            " (L.bonCommande.dateCreated >= :dateTimeDebut or cast(:dateTimeDebut as date) is null) " +
            "AND (L.bonCommande.dateCreated <= :dateTimeFin or  cast(:dateTimeFin as date) is null) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) >= :pointDebut OR :pointDebut = 0) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) <= :pointFin OR :pointFin = 0) " +
            "AND (:fournisseur = '' OR (LOWER(L.cadeauxBA.fournisseur.nom) LIKE LOWER(CONCAT('%', :fournisseur, '%')))) " +
            "AND (:agent = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.nom) LIKE LOWER(CONCAT('%', :agent, '%')))) " +
            "AND (:agence = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')))) " +
            "AND (:cadeau = '' OR (LOWER(L.cadeauxBA.categorieCadeau.libelle) LIKE LOWER(CONCAT('%', :cadeau, '%')))) " +
            "order by L.dateCreated desc ")
    Page<LigneCommande> getLigneCommandeRelatedToAuthenticatedConsultant( String cadeau, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, Integer pointDebut, Integer pointFin, String fournisseur,String agent,String agence, List<Long> representationsIds, Pageable pageable);


    List<LigneCommande> findBycadeauxBAId(Long id);

    @Query("SELECT L.id as numCommande , L.bonCommande.agentCommercial.collaborateur.nom as agent , " +
            "L.bonCommande.agentCommercial.collaborateur.signature as signature ," +
            "L.bonCommande.agentCommercial.collaborateur.entite.nom as agence ," +
            "cast(L.dateCreated as timestamp ) as date , L.cadeauxBA.fournisseur.nom as fournisseur ," +
            "L.cadeauxBA.categorieCadeau.libelle as cadeau , L.bonCommande.nbrPointCredit as nbrPoint," +
            "L.quantite as  quantite " +
            " FROM LigneCommande L " +
            "WHERE"+
            " (L.bonCommande.dateCreated >= :dateTimeDebut or cast(:dateTimeDebut as date) is null) " +
            "AND (L.bonCommande.dateCreated <= :dateTimeFin or  cast(:dateTimeFin as date) is null) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) >= :pointDebut OR :pointDebut = 0) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) <= :pointFin OR :pointFin = 0)" +
            "AND (LOWER(L.cadeauxBA.fournisseur.nom) LIKE LOWER(CONCAT('%', :fournisseur, '%')) OR :fournisseur = '' )" +
            "AND (LOWER(L.cadeauxBA.categorieCadeau.libelle) LIKE LOWER(CONCAT('%', :cadeau, '%')) OR :cadeau = '' )" +
            "AND ((LOWER(L.bonCommande.agentCommercial.collaborateur.nom) LIKE LOWER(CONCAT('%', :agent, '%'))) OR (LOWER(L.bonCommande.agentCommercial.collaborateur.signature) LIKE LOWER(CONCAT('%', :agent, '%'))) or :agent = '' )" +
            //"AND (LOWER(L.bonCommande.agentCommercial.collaborateur.signature) LIKE LOWER(CONCAT('%', :agent, '%')) OR :agent = '' )" +
            "AND (LOWER(L.bonCommande.agentCommercial.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')) OR :agence = '' ) order by L.dateCreated" )
    List<CommandeProjection> findByBonCommandeList(String cadeau, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, Integer pointDebut, Integer pointFin, String fournisseur, String agent/*,String signature*/, String agence);
    @Query("SELECT L.id as numCommande , L.bonCommande.agentCommercial.collaborateur.nom as agent , " +
            "L.bonCommande.agentCommercial.collaborateur.signature as signature ," +
            "L.bonCommande.agentCommercial.collaborateur.entite.nom as agence ," +
            "cast(L.dateCreated as timestamp) as date , L.cadeauxBA.fournisseur.nom as fournisseur ," +
            "L.cadeauxBA.categorieCadeau.libelle as cadeau , L.bonCommande.nbrPointCredit as nbrPoint," +
            "L.quantite as  quantite " +
            "FROM LigneCommande L " +
            "WHERE L.bonCommande.agentCommercial.collaborateur.entite.parent.id = :idEntite " +
            "AND (L.bonCommande.dateCreated >= :dateTimeDebut or cast(:dateTimeDebut as date) is null) " +
            "AND (L.bonCommande.dateCreated <= :dateTimeFin or  cast(:dateTimeFin as date) is null) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) >= :pointDebut OR :pointDebut = 0) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) <= :pointFin OR :pointFin = 0) " +
            "AND (:fournisseur = '' OR (LOWER(L.cadeauxBA.fournisseur.nom) LIKE LOWER(CONCAT('%', :fournisseur, '%')))) " +
            "AND (:agent = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.nom) LIKE LOWER(CONCAT('%', :agent, '%')))) " +
            "AND (:agence = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')))) " +
            "AND (:cadeau = '' OR (LOWER(L.cadeauxBA.categorieCadeau.libelle) LIKE LOWER(CONCAT('%', :cadeau, '%')))) " +
            "order by L.dateCreated desc ")
    List<CommandeProjection> getLigneCommandeRelatedToAuthenticatedRatacheList( String cadeau, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, Integer pointDebut, Integer pointFin, String fournisseur,String agent,String agence, Long idEntite);


    @Query("SELECT L.id as numCommande , L.bonCommande.agentCommercial.collaborateur.nom as agent , " +
            "L.bonCommande.agentCommercial.collaborateur.signature as signature ," +
            "L.bonCommande.agentCommercial.collaborateur.entite.nom as agence ," +
            " cast(L.dateCreated as date ) as date , L.cadeauxBA.fournisseur.nom as fournisseur ," +
            "L.cadeauxBA.categorieCadeau.libelle as cadeau , L.bonCommande.nbrPointCredit as nbrPoint," +
            "L.quantite as  quantite " +
            "FROM LigneCommande L " +
            "WHERE L.bonCommande.agentCommercial.collaborateur.entite.parent.parent.id in :representationsIds and"+
            " (L.bonCommande.dateCreated >= :dateTimeDebut or cast(:dateTimeDebut as date) is null) " +
            "AND (L.bonCommande.dateCreated <= :dateTimeFin or  cast(:dateTimeFin as date) is null) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) >= :pointDebut OR :pointDebut = 0) " +
            "AND ((L.quantite * L.cadeauxBA.nbrPoint) <= :pointFin OR :pointFin = 0) " +
            "AND (:fournisseur = '' OR (LOWER(L.cadeauxBA.fournisseur.nom) LIKE LOWER(CONCAT('%', :fournisseur, '%')))) " +
            "AND (:agent = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.nom) LIKE LOWER(CONCAT('%', :agent, '%')))) " +
            "AND (:agence = '' OR (LOWER(L.bonCommande.agentCommercial.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')))) " +
            "AND (:cadeau = '' OR (LOWER(L.cadeauxBA.categorieCadeau.libelle) LIKE LOWER(CONCAT('%', :cadeau, '%')))) " +
            "order by L.dateCreated desc ")
    List<CommandeProjection> getLigneCommandeRelatedToAuthenticatedConsultantList( String cadeau, LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin, Integer pointDebut, Integer pointFin, String fournisseur,String agent,String agence, List<Long> representationsIds);

}
