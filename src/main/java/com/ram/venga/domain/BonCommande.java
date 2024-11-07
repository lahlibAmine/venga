package com.ram.venga.domain;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ram.venga.model.enumeration.StatutBAEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ram.venga.model.enumeration.EtatBonCommandeEnum;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BonCommande {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "bon_commande_sequence",
            sequenceName = "bon_commande_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bon_commande_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutBAEnum etat;

    @Column(nullable = false)
    private Integer nbrPointCredit;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_commercial_id")
    private Utilisateur agentCommercial;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    private LocalDateTime dateLivraison;
    private LocalDateTime dateReception;


    @LastModifiedDate
    private LocalDateTime lastUpdated;


    @OneToMany(mappedBy = "bonCommande", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER )
    private Set<LigneCommande> ligneCommandes;

    @OneToMany(mappedBy = "bonCommande", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER )
    private Set<Opperation> opperations;
}
