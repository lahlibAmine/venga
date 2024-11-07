package com.ram.venga.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

//@Entity
//@EntityListeners(AuditingEntityListener.class)
//@Getter
//@Setter
public class OrigineEmissionEntite  /*implements Serializable*/ {
  /*  @Id
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_emission_id")
    private OrigineEmission origineEmission;


    @Id
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "entite_id", referencedColumnName = "id")
    private Entite entite;

    @Column(nullable = false)
    private Integer nbrPointBienvenue;*/
}
