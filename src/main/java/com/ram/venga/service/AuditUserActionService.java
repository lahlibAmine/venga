package com.ram.venga.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ram.venga.domain.AuditUserAction;
import com.ram.venga.domain.QAuditUserAction;
import com.ram.venga.domain.Vente;
import com.ram.venga.domain.util.ClassMapperHelper;
import com.ram.venga.mapper.AuditUserActionMapper;
import com.ram.venga.model.AuditUserActionDTO;
import com.ram.venga.model.enumeration.AuditActionEnum;
import com.ram.venga.repos.AuditUserActionRepository;
import com.ram.venga.repos.VenteRepository;
import com.ram.venga.util.DateFormatterUtil;
import com.ram.venga.util.NotFoundException;

@Service
public class AuditUserActionService {

    private final AuditUserActionMapper auditUserActionMapper;
    private final AuditUserActionRepository auditUserActionRepository;
    private final VenteRepository venteRepository;

    public AuditUserActionService(
            final VenteRepository venteRepository,
            final AuditUserActionMapper auditUserActionMapper,
            final AuditUserActionRepository auditUserActionRepository) {

        this.venteRepository = venteRepository;
        this.auditUserActionMapper = auditUserActionMapper;
        this.auditUserActionRepository = auditUserActionRepository;
    }

    /**
     *
     * @param fromDate
     * @param toDate
     * @param auditedObjectName
     * @param auditedFieldName
     * @param oldFieldValue
     * @param newFieldValue
     * @param auditedAction
     * @param username
     * @param numBillet
     * @return
     */
    public Page<AuditUserActionDTO> findAllByFilter(
            String fromDate,
            String toDate,
            String auditedObjectName,
            String auditedFieldName,
            String oldFieldValue,
            String newFieldValue,
            AuditActionEnum auditedAction,
            String username,
            String numBillet, PageRequest pageRequest) {

        BooleanExpression predicate = QAuditUserAction.auditUserAction.id.isNotNull();

        if (fromDate != null && !fromDate.isEmpty()) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.dateCreated.after(
                    DateFormatterUtil.toDateTime(fromDate)));
        }

        if (toDate != null && !toDate.isEmpty() && DateFormatterUtil.toDateTime(toDate).isAfter(
                DateFormatterUtil.toDateTime(fromDate))) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.dateCreated.before(
                    DateFormatterUtil.toDateTime(toDate)));
        }

        if (auditedObjectName != null && !auditedObjectName.isEmpty()) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.auditedObjectName.eq(auditedObjectName));
        }

        if (auditedFieldName != null && !auditedFieldName.isEmpty()) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.auditedFieldName.eq(auditedFieldName));
        }

        if (oldFieldValue != null && !oldFieldValue.isEmpty()) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.oldFieldValue.eq(oldFieldValue));
        }

        if (newFieldValue != null && !newFieldValue.isEmpty()) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.newFieldValue.eq(newFieldValue));
        }

        if (auditedAction != null) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.auditedAction.eq(auditedAction));
        }

        if (username != null && !username.isEmpty()) {
            predicate = predicate.and(QAuditUserAction.auditUserAction.user.login.like("%"+username+"%"));
        }

        if (numBillet != null && !numBillet.isEmpty()) {
            Vente vente = venteRepository.findByNumBillet(numBillet);
            if(vente != null)
                predicate = predicate.and(QAuditUserAction.auditUserAction.auditedObjectId.eq(vente.getId()));
            else
                predicate = predicate.and(QAuditUserAction.auditUserAction.auditedObjectId.eq(0L));
        }

        Page<AuditUserAction> auditUserActions = (Page<AuditUserAction>) auditUserActionRepository.findAll(predicate, pageRequest);

        List<AuditUserActionDTO> dtoList = auditUserActions.getContent().stream()
                .map(auditUserActionMapper::toDto)
                .collect(Collectors.toList());

        this.addDynamicProperties(dtoList);

        return new PageImpl<>(dtoList, auditUserActions.getPageable(), auditUserActions.getTotalElements());

    }

    /**
     *
     * @param AuditActionList
     * @return
     */
    private List<AuditUserActionDTO> addDynamicProperties(List<AuditUserActionDTO> AuditActionList) {

        for(AuditUserActionDTO auditUserAction : AuditActionList) {

            switch (auditUserAction.getAuditedAction()) {
                case CHANGEMENT_SIGNATURE: {
                    Map<String, Object> extractedKVMapProperties = ClassMapperHelper.mapObjectToProperties(
                            venteRepository.findById(auditUserAction.getAuditedObjectId()).get(), auditUserAction.getAuditedAction().getExtraObjectFieldNames());
                    auditUserAction.setAuditedObjectExtraFields(extractedKVMapProperties);
                }
            }
        }
        return AuditActionList;
    }

    /**
     *
     * @return
     */
    public List<AuditUserActionDTO> findAll() {
        Iterable<AuditUserAction> auditUserActions = auditUserActionRepository.findAll(Sort.by("id"));
        Stream<AuditUserAction> auditUserActionStream = StreamSupport.stream(auditUserActions.spliterator(), false);

        return auditUserActionStream.map(auditUserAction -> auditUserActionMapper.toDto(auditUserAction)).toList();
    }

    /**
     *
     * @param id
     * @return
     */
    public AuditUserActionDTO get(final Long id) {
        return auditUserActionRepository.findById(id)
                .map(auditUserAction -> auditUserActionMapper.toDto(auditUserAction))
                .orElseThrow(NotFoundException::new);
    }

    /**
     *
     * @param auditUserActionDTO
     * @return
     */
    public Long create(final AuditUserActionDTO auditUserActionDTO) {
        AuditUserAction auditUserAction;
        auditUserAction = auditUserActionRepository.save(
                auditUserActionMapper.toEntity(auditUserActionDTO));
        return auditUserAction != null? auditUserAction.getId() : -1;
    }

    /**
     *
     * @param id
     * @param auditUserActionDTO
     */
    public void update(final Long id, final AuditUserActionDTO auditUserActionDTO) {
        final AuditUserAction auditUserAction = auditUserActionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        auditUserActionMapper.toEntity(auditUserActionDTO);
        auditUserActionRepository.save(auditUserAction);
    }

    /**
     *
     * @param id
     */
    public void delete(final Long id) {
        auditUserActionRepository.deleteById(id);
    }

}