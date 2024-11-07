package com.ram.venga.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ram.venga.model.enumeration.ImportStatusEnum;
import com.ram.venga.model.enumeration.ScheduleStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ImportedPrimes {
    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "imported_primes_sequence",
            sequenceName = "imported_primes_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "imported_primes_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private Integer nbrPoint;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_emission_id")
    private OrigineEmission origineEmission;


    private String segment;


    private String classeProduit;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;


    private OffsetDateTime treated_at;
    @Enumerated(EnumType.STRING)
    private ImportStatusEnum processing_status;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus schedule_status;
    
    @Column(nullable = true)
    private String fileId;
}
