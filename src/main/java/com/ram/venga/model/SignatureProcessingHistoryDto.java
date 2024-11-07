package com.ram.venga.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignatureProcessingHistoryDto {

    private String oldSignature;

    private String newSignature;

    private String modifierEmail; // email of the user who performed the action

    private OffsetDateTime modificationDate;

    private String numBillet;

}
