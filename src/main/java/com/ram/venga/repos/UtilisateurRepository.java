package com.ram.venga.repos;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.ProfilEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    boolean existsByRefKUserIgnoreCase(String refKUser);

    boolean existsByLoginIgnoreCase(String login);

    boolean existsByEmailIgnoreCase(String email);

    Utilisateur findByEmail(String email);


    Optional<Utilisateur> findUtilisateurByEmail(String email);

    @Query(value = "select C.email from Collaborateur C where C.categorie = 'RATTACHE' and C.entite.id = :idEntite")
    Optional<String> findConcernedRattacheEmail(Long idEntite);

    Utilisateur findByCollaborateurId(Long collaborateur);
    Optional<Utilisateur> findByRefKUser(String Ref);

    @Query(value = "select U.id from Utilisateur U where U.collaborateur.entite.id in :idEntites")
    List<Long> findIdByEntiteIdIn(List<Long> idEntites);
    @Query("select u from Utilisateur u where lower(u.email) = :email ")
    Utilisateur findByEmailLower(String email);
    @Query("SELECT u FROM Utilisateur u WHERE (u.collaborateur.categorie = 'COMMERCIAL' AND LOWER(u.collaborateur.signature) = lower(:signature)) OR (u.collaborateur.categorie <> 'COMMERCIAL' AND lower(u.email) = lower(:signature))")
    Utilisateur findBySignatureLower(String signature);

    @Query(value = "select C.email from Collaborateur C where C.categorie = 'FONCTIONNEL'")
    List<String> findConcernedFunctAdminsEmails();
}
