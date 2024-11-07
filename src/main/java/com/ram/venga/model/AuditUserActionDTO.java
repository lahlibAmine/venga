package com.ram.venga.model;

import java.time.OffsetDateTime;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ram.venga.model.enumeration.AuditActionEnum;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuditUserActionDTO {
   
	private Long id;

    @NotNull
    @Size(max = 32)
    private String auditedObjectName;

    @NotNull
    @Size(max = 32)
    private Long auditedObjectId;

    @NotNull
    @Size(max = 32)
    private String auditedFieldName;

    @NotNull
    @Size(max = 32)
    private String oldFieldValue;

    @NotNull
    @Size(max = 32)
    private String newFieldValue;

    @NotNull
    private AuditActionEnum auditedAction;

    @NotNull
    private Long user;
    
    @NotNull
    private String username;
    
    @NotNull
    private OffsetDateTime dateCreated;
    
    /**
     * Other fields...
     * Ex: extraObjectFieldKVMap = "numBillet:131414,statutBillet:EMIS"
     */
	private Map<String, Object> auditedObjectExtraFields;
    
    
    public AuditUserActionDTO(@NotNull Long user, @NotNull String username, @NotNull @Size(max = 32) String auditedObjectName,
			@NotNull Long auditedObjectId, @NotNull @Size(max = 32) String auditedFieldName,
			@NotNull @Size(max = 32) String oldFieldValue, @NotNull @Size(max = 32) String newFieldValue,
			@NotNull AuditActionEnum auditedAction, Map<String, Object> auditedObjectExtraFields) {
		super();
		this.user = user;
		this.username = username;
		this.auditedObjectName = auditedObjectName;
		this.auditedObjectId = auditedObjectId;
		this.auditedFieldName = auditedFieldName;
		this.oldFieldValue = oldFieldValue;
		this.newFieldValue = newFieldValue;
		this.auditedAction = auditedAction;
		this.auditedObjectExtraFields = auditedObjectExtraFields;

	}
    
	public static AuditUserActionDTO getInstance(AuditActionEnum action, Long auditedObjectId, Long userId, 
			String username, String oldFieldValue, String newFieldValue) {
				
		switch (action) {
		case CHANGEMENT_SIGNATURE, OTP: {
			return new AuditUserActionDTO(userId, username, action.getAuditedObjectName(), 
				auditedObjectId, action.getAuditedFieldName(), oldFieldValue, newFieldValue, action, null);
			
		}
			default:
			throw new IllegalArgumentException("Unexpected value: " + action);
		}
		
	}

}
