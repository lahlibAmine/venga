package com.ram.venga.domain;

import javax.persistence.*;

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
public class Fournisseur {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "fournisseur_sequence",
            sequenceName = "fournisseur_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fournisseur_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String nom;

    @Column(length = 16)
    private String telephone;

    @Column(length = 64)
    private String email;

    @Column
    private String adresse;

    @OneToMany(mappedBy = "fournisseur",fetch = FetchType.EAGER)
    private Set<CadeauxBA> cadeauxBAs;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

}
