package com.ram.venga.domain;

import com.ram.venga.model.enumeration.StatutVenteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
public class JournalRapprochementView {

    @Id
    private Long id;
    @Column(nullable = false, length = 16)
    private String numBillet;
    @Column(nullable = false, length = 32)
    private Integer nbrCoupon;

    @Column(nullable = false, length = 32)
    private Integer nbrCouponNonRapprocher;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutVenteEnum statutVente;

    @Column(nullable = false)
    private Boolean venteRapproche;

    private LocalDate lastUpdated;
    private boolean isArchived;
}
