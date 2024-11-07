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
public class Pays {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "pays_sequence",
            sequenceName = "pays_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pays_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String codeIso;

    @Column(nullable = false, unique = true, length = 64)
    private String nom;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

}
