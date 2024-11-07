package com.ram.venga.model.enumeration;


public enum AuditActionEnum {

    CHANGEMENT_SIGNATURE("Vente", "signatureAgent", "numBillet,statutBillet"),
    OTP("Utilisateur", "last_otp", "last_otp_is_valid");

	private final String auditedObjectName;
    private final String auditedFieldName;
    private final String extraObjectFieldNames;

    AuditActionEnum(String auditedObjectName, String auditedFieldName, String extraObjectFieldNames) {
        this.auditedObjectName = auditedObjectName;
        this.auditedFieldName = auditedFieldName;
        this.extraObjectFieldNames = extraObjectFieldNames;
    }

    // Add a method to perform the lookup
    public static AuditActionEnum getByAuditedObjectName(String auditedObjectName) {
        for (AuditActionEnum value : values()) {
            if (value.getAuditedObjectName().equals(auditedObjectName)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No enum constant with extraObjectFieldName: " + auditedObjectName);
    }
    
    public String getAuditedObjectName() {
        return auditedObjectName;
    }

    public String getAuditedFieldName() {
        return auditedFieldName;
    }
    
    public String getExtraObjectFieldNames() {
    	return extraObjectFieldNames;
    }

}
