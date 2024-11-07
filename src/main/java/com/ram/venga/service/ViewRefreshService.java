package com.ram.venga.service;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
@Service
@Transactional
public class ViewRefreshService {

    @PersistenceContext
    private EntityManager entityManager;

    public void refreshJournalRapprochementView() {
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW journal_rapprochement_view").executeUpdate();
    }
    public void refreshExportEmissionView() {
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW export_emission_view").executeUpdate();
    }
}