package com.ram.venga.model.securite;

import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.UtilisateurDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class AuthResponseDto {
    private UtilisateurDTO userDto;
    private String message;
}
