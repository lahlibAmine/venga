package com.ram.venga.service;

import com.ram.venga.domain.AuditUserAction;
import com.ram.venga.domain.Vente;
import com.ram.venga.model.SignatureProcessingHistoryDto;
import com.ram.venga.repos.AuditUserActionRepository;
import com.ram.venga.repos.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* This class is related to AuditUserAction entity/table, 
*  and it is used to perform operations on the AuditUserAction entity/table
*  that are related to the history of the signature processing such as 'Changement Signature'
* 
* */

@Service
@RequiredArgsConstructor
public class SignatureProcessingHistoryService {

    private final AuditUserActionRepository auditUserActionRepository;
    private final VenteRepository venteRepository;

    @Transactional
    // This method is used to get all the logs related to vente change signature by keyword (numBillet, oldSignature, newSignature, modifierEmail, modificationDate)
    public Page<SignatureProcessingHistoryDto> getLogsByKeyword(String keyword, PageRequest pageRequest) {

        // Get all the vente ids that have had a 'CHANGEMENT_SIGNATURE' action performed on them
        List<Long> venteIdsList = auditUserActionRepository.findAllVenteIdsRelatedToVenteChangeSignature();

        // Get all the ventes that have the numBillet containing the keyword and are in the venteIdsList
        List<Vente> ventesFilteredByKeyword = keyword != null && !keyword.isEmpty() ?
                venteRepository.findAllByIdInAndNumBilletContains(venteIdsList, keyword.replace("147-", ""))
                : List.of();

        // If the search keyword was a numBillet
        if (!ventesFilteredByKeyword.isEmpty()) {
            return searchProcessByNumBillet(ventesFilteredByKeyword , pageRequest);
        }else { // If the search keyword was one of 'oldSignature', 'newSignature', 'modifierEmail' or 'modificationDate'
            return searchProcessByOthers(keyword, pageRequest);
        }

    }

    // This method is used to get all the logs related to vente change signature by numBillet
    private Page<SignatureProcessingHistoryDto> searchProcessByNumBillet( List<Vente> ventesFilteredByKeyword , PageRequest pageRequest) {
        // Create a map of the vente ids to the numBillet
        Map<Long, String> venteIdsToNumBillet = ventesFilteredByKeyword.stream()
                .collect(Collectors.toMap(Vente::getId, Vente::getNumBillet));

        // Get the vente ids that have the numBillet containing the keyword
        Collection<Long> venteIdsFilteredByKeyword = venteIdsToNumBillet.keySet();

        // Get the auditUserActions that have the auditedObjectId in the venteIdsFilteredByKeyword
        Page<AuditUserAction> auditUserActionsPage = auditUserActionRepository.findAllChangementSignatureByAuditedObjectIdIn(venteIdsFilteredByKeyword, pageRequest);

        // Map the auditUserActions to signatureProcessingHistoryDto
        List<SignatureProcessingHistoryDto> dtoList =
                signatureProcessingHistoryDtoBuilder(auditUserActionsPage.getContent(), venteIdsToNumBillet);

        return new PageImpl<>(dtoList, auditUserActionsPage.getPageable(), auditUserActionsPage.getTotalElements());
    }

    private Page<SignatureProcessingHistoryDto> searchProcessByOthers(String keyword, PageRequest pageRequest){

        // Get the auditUserActions that have the oldFieldValue, newFieldValue, dateCreated or user.email containing the keyword
        Page<AuditUserAction> auditUserActionsPage = auditUserActionRepository.findAllChangementSignatureAndKeyword(keyword, pageRequest);

        // Get vente ids from auditUserActions
        List<Long> venteIdsPage = auditUserActionsPage.getContent().stream()
                .map(AuditUserAction::getAuditedObjectId).toList();

        // Get the ventes that have the vente ids from the auditUserActions
        List<Vente> ventes = venteRepository.findAllById(venteIdsPage);

        // Create a map of the vente ids to the numBillet
        Map<Long, String> auditedObjectIdsToNumBillet = ventes.stream()
                .collect(Collectors.toMap(Vente::getId, Vente::getNumBillet));

        // Map the auditUserActions to SignatureProcessingHistoryDto
        List<SignatureProcessingHistoryDto> dtoList =
                signatureProcessingHistoryDtoBuilder(auditUserActionsPage.getContent(), auditedObjectIdsToNumBillet);

        return new PageImpl<>(dtoList, auditUserActionsPage.getPageable(), auditUserActionsPage.getTotalElements());
    }

    private List<SignatureProcessingHistoryDto> signatureProcessingHistoryDtoBuilder(List<AuditUserAction> auditUserActions, Map<Long, String> venteIdsToNumBillet) {

        return auditUserActions.stream()
                .map(auditUserAction -> SignatureProcessingHistoryDto.builder()
                        .oldSignature(auditUserAction.getOldFieldValue())
                        .newSignature(auditUserAction.getNewFieldValue())
                        .modifierEmail(auditUserAction.getUser().getEmail())
                        .modificationDate(auditUserAction.getDateCreated())
                        .numBillet(venteIdsToNumBillet.get(auditUserAction.getAuditedObjectId()))
                        .build()
                ).toList();


    }
}
