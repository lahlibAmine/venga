package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ResetPasswordDTO {
    @NotEmpty
    @NotNull
    private String password;
    @NotEmpty
    @NotNull
    private String confirmPassword;
}
