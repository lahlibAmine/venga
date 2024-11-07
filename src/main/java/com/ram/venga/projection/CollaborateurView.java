package com.ram.venga.projection;

import com.ram.venga.domain.Entite;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.CiviliteEnum;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public interface CollaborateurView {

    Long getIdCollaborateur();

     String getCode();

    String getNomCollaborateur();
    String getNomEntite();

     String getSignature();

    String getAdresseAgence();

    Integer getSoldePoint();


    String getEmailAgence();
    String getEmailCollaborateur();


    String getFax();

}
