package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateOtpByEmailDTO {
    private String email;
    private String otpCode;
}
