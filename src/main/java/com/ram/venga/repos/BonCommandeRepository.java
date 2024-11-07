package com.ram.venga.repos;

import com.ram.venga.domain.BonCommande;
import com.ram.venga.domain.LigneCommande;
import com.ram.venga.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface BonCommandeRepository extends JpaRepository<BonCommande, Long> {

    boolean existsByReferenceIgnoreCase(String reference);

    @Query("select B from BonCommande B where B.agentCommercial.id = :idUser")
    List<BonCommande> findByAgentCommercial_Id(Long idUser);
    BonCommande findByReference(String reference);
    boolean existsByAgentCommercial(Utilisateur utilisateur);
    BonCommande findByAgentCommercial(Utilisateur utilisateur);


    /* @Query("select B.cadeauxBAS from  BonCommande B where B.agentCommercial.id = :idCollaborateur")
    Set<CadeauxBA> getCadeauByUser(Long idCollaborateur);*/

}
