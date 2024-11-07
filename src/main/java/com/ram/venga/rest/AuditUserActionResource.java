package com.ram.venga.rest;

import java.util.List;

import javax.validation.Valid;

import com.ram.venga.model.SignatureProcessingHistoryDto;
import com.ram.venga.model.enumeration.AuditActionEnum;
import com.ram.venga.service.SignatureProcessingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ram.venga.model.AuditUserActionDTO;
import com.ram.venga.service.AuditUserActionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping(value = "/api/v1/audit/actions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuditUserActionResource {

    private final AuditUserActionService auditUserActionService;
    private final SignatureProcessingHistoryService signatureProcessingHistoryService;

  @GetMapping("/filtred")
    public ResponseEntity<Page<AuditUserActionDTO>> findAllByFilter(
    		@RequestParam(required = false) String fromDate,
    		@RequestParam(required = false) String toDate,
            @RequestParam(required = false) String auditedObjectName,
            @RequestParam(required = false) String auditedFieldName,
            @RequestParam(required = false) String oldFieldValue,
            @RequestParam(required = false) String newFieldValue,
            @RequestParam(required = false) AuditActionEnum auditedAction,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String numBillet,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<AuditUserActionDTO> auditActions = auditUserActionService.findAllByFilter(
        		fromDate, toDate, auditedObjectName, auditedFieldName, oldFieldValue,
        		newFieldValue, auditedAction, username, numBillet, PageRequest.of(page, size));

        return ResponseEntity.ok(auditActions);
    }

    @GetMapping("/change-signature-history/search")
    public ResponseEntity<Page<SignatureProcessingHistoryDto>> changeSignatureHistorySearch(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                signatureProcessingHistoryService.getLogsByKeyword(keyword, PageRequest.of(page, size))
        );
    }
    
    @GetMapping("/")
    public ResponseEntity<List<AuditUserActionDTO>> getAllAuditUserActions() {
        return ResponseEntity.ok(auditUserActionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditUserActionDTO> getAuditUserAction(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(auditUserActionService.get(id));
    }

    @PostMapping("/create")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAuditUserAction(
            @RequestBody @Valid final AuditUserActionDTO auditUserActionDTO) {
        final Long createdId = auditUserActionService.create(auditUserActionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Long> updateAuditUserAction(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final AuditUserActionDTO auditUserActionDTO) {
        auditUserActionService.update(id, auditUserActionDTO);
        return ResponseEntity.ok(id);
    }

    @PostMapping("delete/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAuditUserAction(@PathVariable(name = "id") final Long id) {
        auditUserActionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

