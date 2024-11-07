package com.ram.venga.model;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ImageDTO {

    private Long id;

    @Size(max = 255)
    private String url;

    private Long offre;
    private String description;
    private String origine;

    public ImageDTO(Long id, String url, Long offre, String description, String origine) {
        this.id = id;
        this.url = url;
        this.offre = offre;
        this.description = description;
        this.origine = origine;
    }

    public ImageDTO() {

    }
}
