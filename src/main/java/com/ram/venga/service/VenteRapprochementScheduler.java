package com.ram.venga.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VenteRapprochementScheduler {

    private final JournalRapprochementService journalRapprochementService;

    public VenteRapprochementScheduler(JournalRapprochementService journalRapprochementService) {
        this.journalRapprochementService = journalRapprochementService;
    }


  /*  @Scheduled(cron = "0 0 2 * * ?") // Exécution tous les jours à 2h du matin
    public void performVenteRapprochement() {
        journalRapprochementService.create();
        // Votre code actuel pour le rapprochement des ventes avec les recettes brutes
        // ...
    }*/
}

