package com.ram.venga.projection;

import java.time.LocalDateTime;

public interface CheckSignatureVenteRepost {
    String getOrigineEmission();
    String getCodeIata();
    String getNumBillet();
    String getNbrCoupon();
    String getNbrPoint();
    String getSignature();
    LocalDateTime getDateEmission();
    String getRepresentation();
    String getPortfeuille();
}
