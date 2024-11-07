package com.ram.venga.projection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.domain.Fournisseur;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.EtatBonCommandeEnum;
import javax.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Set;

public interface BonCommandeView {

    Long getId();

    String getReference();

    LocalDateTime getDate();

    EtatBonCommandeEnum getEtat();

    Integer getNbrPointCredit();

    Long getAgentCommercial();

    LocalDateTime getDateCreated();

    LocalDateTime getLastUpdated();

    Set<CadeauxBA> getCadeauxBAs();

    Fournisseur getFournisseur();

    void setCadeauxBAs(Set<CadeauxBA> cadeauxBAS);

    void addCadeauxBA(CadeauxBA cadeauxBA);

    void removeCadeauxBA(CadeauxBA cadeauxBA);
}
