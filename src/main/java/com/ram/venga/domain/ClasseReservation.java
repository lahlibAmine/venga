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
public class ClasseReservation {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "classe_reservation_sequence",
            sequenceName = "classe_reservation_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "classe_reservation_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @Column(length = 32)
    private String libelle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classe_produit_id", nullable = false)
    private ClasseProduit classeProduit;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

}
