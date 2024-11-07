package com.ram.venga.model;

import com.ram.venga.model.enumeration.OTPTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OtpRequestDTO {
    @NotNull
    private OTPTypeEnum action;
}
