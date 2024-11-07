package com.ram.venga.domain;

import javax.persistence.*;

import java.time.LocalDateTime;
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
public class Opperation {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "opperation_sequence",
            sequenceName = "opperation_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "opperation_sequence"
    )
    private Long id;

    @Column
    private LocalDateTime date;

    @Column
    private Integer debit;

    @Column
    private Integer credit;

    @Column
    private Integer solde;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recette_id")
    private RecetteBrute recetteBrute;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bon_commande_id")
    private BonCommande bonCommande;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;
    private String signature;

}
