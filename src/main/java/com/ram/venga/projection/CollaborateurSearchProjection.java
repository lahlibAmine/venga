package com.ram.venga.projection;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;


public interface  CollaborateurSearchProjection {

    String getNom();
     String getPrenom();
     String getEmail();
     String getTelephone();
     String getAgenceNom();
     String getAgenceCode();
     String getPortfeuilleNom();
     String getRepresentationNom();
 OffsetDateTime getDateCreation();
     String getStatut();
     String getSignature();


}
