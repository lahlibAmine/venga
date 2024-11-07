package com.ram.venga.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class LigneCommande {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "ligne_commande_sequence",
            sequenceName = "ligne_commande_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ligne_commande_sequence"
    )
    private Long id;

    @Column
    private Integer quantite;

    @Transient
    private Integer resultat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cadeauxBA_id")
    private CadeauxBA cadeauxBA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bon_commande_id")
    private BonCommande bonCommande;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

    public Integer getResultat() {
        return quantite * cadeauxBA.getNbrPoint();
    }


}
