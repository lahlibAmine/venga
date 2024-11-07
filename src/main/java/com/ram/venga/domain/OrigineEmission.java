package com.ram.venga.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OrigineEmission {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "origine_emission_sequence",
            sequenceName = "origine_emission_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "origine_emission_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String nom;

    @OneToMany(mappedBy = "origineEmission")
    private Set<CadeauxBA> cadeauxBAs;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pays_id")
    private Pays pays;

    @Column(nullable = false, updatable = false)
    private LocalDate dateCreated;

    private LocalDate lastUpdated;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "devise_id")
    private Devise devise;

    @Column(nullable = false)
    private Integer nbrPointBienvenue;
}

