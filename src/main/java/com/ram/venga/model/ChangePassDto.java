package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassDto {
    private String oldPass;
    private String newPass;
}