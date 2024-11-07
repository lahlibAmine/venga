package com.ram.venga.model.securite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class AuthRequestDto {
    private String email;
    private String password;
}
