package com.ram.venga.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import java.time.OffsetDateTime;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prime {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "prime_sequence",
            sequenceName = "prime_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "prime_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private Integer nbrPoint;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_emission_id")
    private OrigineEmission origineEmission;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "segment_id")
    private Segment segment;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classe_produit_id")
    private ClasseProduit classeProduit;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;


    public Long getIdOrigineEmission(){
        return origineEmission.getId();
    }
    public Long getIdSegment(){
        return segment.getId();
    }
    public Long getIdClasseProduit(){
        return classeProduit.getId();
    }


}
