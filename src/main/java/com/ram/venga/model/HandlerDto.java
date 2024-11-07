package com.ram.venga.model;

import com.ram.venga.model.enumeration.HandlerEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandlerDto {
    private HandlerEnum status;
    private String message;
}
