package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotificationDTO {

    private Long id;

    @NotNull
    @Size(max = 32)
    private String objet;

    @NotNull
    private Boolean consulte;

    private Long utilisateur;

}
