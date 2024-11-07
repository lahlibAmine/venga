package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateAgentPersonalInfosDTO {
    @NotEmpty
    private String phoneNumber;
    private String fonction;
    @NotEmpty
    @Size(min = 10,max = 10)
    private String otpCode;
}
