package com.ram.venga.projection;

import com.ram.venga.model.OrigineEmissionSignatureDto;
import com.ram.venga.model.SegmentRecetteDTO;
import com.ram.venga.model.enumeration.StatutBilletEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;

import java.time.LocalDate;
import java.util.List;

public interface ListEmissionProjection {

    String getNumBillet();
    String getPnr();
    Integer getNbrCoupon();
    LocalDate getDateEmission();
    String getSignatureAgent();
    String getCodeIATA();
    String getNomAgence();
    Integer getNbrPoint();
    StatutBilletEnum getStatutBillet();
    String getNomAgent();
    String getEmailAgence();
    String getMobileAgent();
    String getNomAgenceAgent();
    List<RecetteProjection> getSegmentDTOList();


}
