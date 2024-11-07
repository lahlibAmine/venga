package com.ram.venga.repos;

import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.AuditActionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;
import com.ram.venga.domain.AuditUserAction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface AuditUserActionRepository extends PagingAndSortingRepository<AuditUserAction, Long>,
        QuerydslPredicateExecutor<AuditUserAction> {

    Page<AuditUserAction> findAll(Predicate predicate, Pageable Pageable);

    @Query("SELECT a FROM AuditUserAction a WHERE a.auditedAction = 'CHANGEMENT_SIGNATURE' " +
            "AND a.auditedObjectName = 'Vente' " +
            "AND a.auditedFieldName = 'signatureAgent' " +
            "AND (a.oldFieldValue LIKE %:keyword% OR " +
            "a.newFieldValue LIKE %:keyword% OR " +
            "cast(a.dateCreated as string)  LIKE %:keyword% OR " +
            "a.user.email LIKE %:keyword%)")
    Page<AuditUserAction> findAllChangementSignatureAndKeyword(String keyword, Pageable Pageable);

    @Query("SELECT a FROM AuditUserAction a WHERE a.auditedAction = 'CHANGEMENT_SIGNATURE' " +
            "AND a.auditedObjectId IN :auditedObjectIds")
    Page<AuditUserAction> findAllChangementSignatureByAuditedObjectIdIn(Collection<Long> auditedObjectIds, Pageable Pageable);

    boolean existsByUser(Utilisateur utilisateur);

    AuditUserAction findByUser(Utilisateur utilisateur);

    /*
     * This method is used to fetch all the IDs of the 'Vente' objects that have had a 'CHANGEMENT_SIGNATURE' action performed on them.
     * The 'CHANGEMENT_SIGNATURE' action refers to a change in the 'signatureAgent' field of the 'Vente' object.
     * The method uses a custom query to fetch the 'auditedObjectId' from the 'AuditUserAction' table where 'auditedObjectName' is 'Vente',
     * 'auditedAction' is 'CHANGEMENT_SIGNATURE' and 'auditedFieldName' is 'signatureAgent'.
     * The result is a list of IDs (Long) of all 'Vente' objects that match these criteria.
     */
    @Query("SELECT a.auditedObjectId FROM AuditUserAction a WHERE a.auditedObjectName = 'Vente' AND a.auditedAction = 'CHANGEMENT_SIGNATURE' AND a.auditedFieldName = 'signatureAgent'")
    List<Long> findAllVenteIdsRelatedToVenteChangeSignature();
}

