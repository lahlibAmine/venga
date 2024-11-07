package com.ram.venga.util;

import com.ram.venga.model.ConcoursDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<DateRangeValid, Object> {

    @Override
    public void initialize(DateRangeValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof ConcoursDTO) {
            ConcoursDTO entity = (ConcoursDTO) obj;
            if (entity.getDateDebutVente() == null || entity.getDateFinVente() == null) {
                return true; // ou false, selon si vous voulez gérer les dates nulles
            }
            return entity.getDateFinVente().isAfter(entity.getDateDebutVente());
        }
        return true;
    }
}
