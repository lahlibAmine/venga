package com.ram.venga.domain;

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
public class CategorieCadeau {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "categorie_cadeau_sequence",
            sequenceName = "categorie_cadeau_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "categorie_cadeau_sequence"
    )
    private Long id;

    @Column(
            nullable = true,
            unique = false,
            length = 16)
    private String code;

    @Column(nullable = false,unique = true, length = 32)
    private String libelle;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

}

