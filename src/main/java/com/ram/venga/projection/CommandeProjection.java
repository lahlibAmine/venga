package com.ram.venga.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface CommandeProjection {
    String getNumCommande();
    String getAgent();
    String getSignature();
    String getAgence();
    LocalDate getDate();
    String getFournisseur();
    String getCadeau();
    String getNbrPoint();
    String getQuantite();

}
