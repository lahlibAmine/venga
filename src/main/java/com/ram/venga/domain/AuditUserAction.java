package com.ram.venga.domain;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.querydsl.core.annotations.QueryEntity;
import com.ram.venga.model.enumeration.AuditActionEnum;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@QueryEntity
public class AuditUserAction {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "audit_user_action_sequence",
            sequenceName = "audit_user_action_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "audit_user_action_sequence"
    )
    private Long id;

    @Column(nullable = false, length = 32)
    private String auditedObjectName;

    @Column(nullable = false)
    private Long auditedObjectId;

    @Column(nullable = false, length = 32)
    private String auditedFieldName;

    @Column(nullable = false, length = 32)
    private String oldFieldValue;

    @Column(nullable = false, length = 32)
    private String newFieldValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditActionEnum auditedAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Utilisateur user;
    
    @Column(nullable = false, length = 32)
    private String username;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
