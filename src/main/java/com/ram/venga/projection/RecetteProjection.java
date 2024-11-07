package com.ram.venga.projection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public interface RecetteProjection {
    String getNumVol();
    String getNumCoupon();
    String getCodeIATA();
    LocalDateTime getDateTransport();
    String geteEscaleDepart();
    String getEscaleDestination();
    String getClassReservation();
    String getClassProduit();
    Integer getPointGagne();
    double getMontantBrut();
    Boolean getRecetteRapproche();
    Boolean getRecetteIntegre();

}
