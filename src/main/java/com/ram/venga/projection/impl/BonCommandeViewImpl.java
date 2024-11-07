package com.ram.venga.projection.impl;

import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.domain.Fournisseur;
import com.ram.venga.model.enumeration.EtatBonCommandeEnum;
import com.ram.venga.projection.BonCommandeView;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class BonCommandeViewImpl implements BonCommandeView {

    // Other properties and methods

    private Set<CadeauxBA> cadeauxBAs = new HashSet<>();


    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public LocalDateTime getDate() {
        return null;
    }

    @Override
    public EtatBonCommandeEnum getEtat() {
        return null;
    }

    @Override
    public Integer getNbrPointCredit() {
        return null;
    }

    @Override
    public Long getAgentCommercial() {
        return null;
    }

    @Override
    public LocalDateTime getDateCreated() {
        return null;
    }

    @Override
    public LocalDateTime getLastUpdated() {
        return null;
    }

    @Override
    public Set<CadeauxBA> getCadeauxBAs() {
        return cadeauxBAs;
    }

    @Override
    public Fournisseur getFournisseur() {
        return null;
    }

    @Override
    public void setCadeauxBAs(Set<CadeauxBA> cadeauxBAS) {
        this.cadeauxBAs = cadeauxBAS;
    }

    @Override
    public void addCadeauxBA(CadeauxBA cadeauxBA) {
        cadeauxBAs.add(cadeauxBA);
    }

    @Override
    public void removeCadeauxBA(CadeauxBA cadeauxBA) {
        cadeauxBAs.remove(cadeauxBA);
    }

}
