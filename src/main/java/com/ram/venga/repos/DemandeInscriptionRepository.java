package com.ram.venga.repos;

import com.ram.venga.domain.DemandeInscription;
import com.ram.venga.domain.Utilisateur;

import java.time.Year;
import java.util.List;

import com.ram.venga.model.enumeration.StatutDemandeEnum;
import com.ram.venga.model.enumeration.TypeDemandeEnum;
import com.ram.venga.projection.inscriptionsEvolutionView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;


public interface DemandeInscriptionRepository extends JpaRepository<DemandeInscription, Long> {

    List<DemandeInscription> findAllByValidateurs(Utilisateur utilisateur);

    DemandeInscription findByCollaborateurId(Long id);

    @Query("SELECT D FROM DemandeInscription D " +
            " inner join Utilisateur u on u.id = D.collaborateur.utilisateur.id WHERE D.statut = :statutDemandeEnum  and D.typeDemande = :typeDemande and ((:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND (" +
            "   TO_CHAR(D.id, 'FM999999999999999999999999') LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.entite.nom)  LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.nom)  LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.prenom) LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.email)  LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(CONCAT(D.collaborateur.nom, ' ', D.collaborateur.prenom)) LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(CONCAT( D.collaborateur.prenom , ' ', D.collaborateur.nom)) LIKE lower(CONCAT('%', :keyword, '%'))" +
            "))) ORDER BY D.dateDemande DESC")
    Page<DemandeInscription> findByStatut(@Param("statutDemandeEnum") StatutDemandeEnum statutDemandeEnum,
                                          @Param("keyword") String keyword,
                                          TypeDemandeEnum typeDemande,
                                          Pageable pageable);

    @Query("select DISTINCT D from DemandeInscription D join D.validateurs v " +
            "where D.collaborateur.categorie = 'COMMERCIAL' and D.typeDemande =:typeDemande " +
            "and D.statut <> 'EN_COURS' " +
            "and D.statut <> 'TRAITE' " +
            "and D.collaborateur.entite.id in :idEntite " +
            "and (LOWER(D.collaborateur.entite.nom) LIKE LOWER(CONCAT('%',:keyword,'%')) or " +
            "LOWER(D.collaborateur.nom) LIKE LOWER(CONCAT('%',:keyword,'%')) or " +
            "LOWER(D.collaborateur.prenom) LIKE LOWER(CONCAT('%',:keyword,'%')) or " +
            "LOWER(v.collaborateur.nom) LIKE LOWER(CONCAT('%',:keyword,'%')) or " +
            "LOWER(v.collaborateur.prenom) LIKE LOWER(CONCAT('%',:keyword,'%')) or " +
            "LOWER(cast(D.statut as  string )) LIKE LOWER(CONCAT('%',:keyword,'%')) or " +
            "LOWER(D.collaborateur.email) LIKE LOWER(CONCAT('%',:keyword,'%')) or " +
            ":keyword = '') " +
            "order by D.dateModification desc ")
    Page<DemandeInscription> findAllByRattacherStatutNot(Pageable pageable,List<Long> idEntite,String keyword, TypeDemandeEnum typeDemande );

    @Query("select to_char( D.dateDemande,'YYYY-MM') as time, count (D.id) as inscriptions from DemandeInscription D where  D.collaborateur.categorie = 'COMMERCIAL' and (year(D.dateDemande) >= coalesce( :dateDebut , year (D.dateDemande))" +
            "and year(D.dateDemande) <= coalesce( :dateFin , year (D.dateDemande)))  group by to_char( D.dateDemande,'YYYY-MM') order by to_char( D.dateDemande,'YYYY-MM')")
    List<inscriptionsEvolutionView> getEvolutionInscriptionMounth(int dateDebut, int dateFin);

    @Query("select to_char( D.dateDemande,'YYYY') as time, count (D.id) as inscriptions from DemandeInscription D where D.collaborateur.categorie = 'COMMERCIAL' and (year(D.dateDemande) >=  :debutYear or:debutYear = 0)" +
            "and (year(D.dateDemande) <= :finYear or :finYear = 0 ) group by to_char( D.dateDemande,'YYYY') order by to_char( D.dateDemande,'YYYY')")
    List<inscriptionsEvolutionView> getEvolutionInscriptionYear(int debutYear, int finYear);

    @Query("SELECT D FROM DemandeInscription D" +
            " inner join Utilisateur u on u.id = D.collaborateur.utilisateur.id WHERE D.collaborateur.entite.id in :idEntite and D.typeDemande = :typeDemande and D.statut = :statutDemandeEnum  and ((:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND (" +
            "   TO_CHAR(D.id, 'FM999999999999999999999999') LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.entite.nom)  LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.nom)  LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.prenom) LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(D.collaborateur.email)  LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(CONCAT(D.collaborateur.nom, ' ', D.collaborateur.prenom)) LIKE lower(CONCAT('%', :keyword, '%'))" +
            "   OR lower(CONCAT( D.collaborateur.prenom , ' ', D.collaborateur.nom)) LIKE lower(CONCAT('%', :keyword, '%'))" +
            "))) ORDER BY D.dateDemande DESC")
    Page<DemandeInscription> findByStatutAndEntite(StatutDemandeEnum statutDemandeEnum, String keyword, List<Long> idEntite, TypeDemandeEnum typeDemande, Pageable pageable);

    @Query("select to_char( D.dateDemande,'YYYY') as time, count (D.id) as inscriptions from DemandeInscription D where  D.collaborateur.categorie = 'COMMERCIAL'  and D.collaborateur.utilisateur.active = true and D.collaborateur.entite.id in :idEntite and (year(D.dateDemande) >=  :debut or:debut = 0)" +
            "and (year(D.dateDemande) <= :fin or :fin = 0 ) group by to_char( D.dateDemande,'YYYY') order by to_char( D.dateDemande,'YYYY')")
    List<inscriptionsEvolutionView> getEvolutionInscriptionYearEntite(int debut, int fin, List<Long> idEntite);

    @Query("select to_char( D.dateDemande,'YYYY-MM') as time, count (D.id) as inscriptions from DemandeInscription D where  D.collaborateur.categorie = 'COMMERCIAL' and D.collaborateur.entite.id in :idEntite and (year(D.dateDemande) >= coalesce( :debutYear , year (D.dateDemande))" +
            "and year(D.dateDemande) <= coalesce( :finYear , year (D.dateDemande)))  group by to_char( D.dateDemande,'YYYY-MM') order by to_char( D.dateDemande,'YYYY-MM')")
    List<inscriptionsEvolutionView> getEvolutionInscriptionMounthEntite(int debutYear, int finYear,List<Long> idEntite);

    @Query("SELECT DISTINCT D " +
            "FROM DemandeInscription D " +
            "JOIN D.validateurs v " +
            "WHERE D.collaborateur.categorie = 'COMMERCIAL' and D.typeDemande =:typeDemande " +
            "AND D.statut NOT IN ('EN_COURS', 'TRAITE') " + // Use NOT IN for multiple conditions
            "AND (:keyword IS NULL OR :keyword = '') " + // Parentheses added for clarity
            "OR (:keyword <> '' " + // Parentheses added for clarity
            "AND (LOWER(D.collaborateur.entite.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(D.collaborateur.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(D.collaborateur.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.collaborateur.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.collaborateur.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(cast(D.statut AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(D.collaborateur.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR :keyword = '')) " +
            "ORDER BY D.dateModification DESC")
    Page<DemandeInscription> findAllByAdminStatutNot(Pageable pageable, String keyword,TypeDemandeEnum typeDemande);


}

