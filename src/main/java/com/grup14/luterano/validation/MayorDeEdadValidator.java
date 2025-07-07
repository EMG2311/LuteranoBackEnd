package com.grup14.luterano.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class MayorDeEdadValidator implements ConstraintValidator<MayorDeEdad, Date> {

    @Override
    public boolean isValid(Date fechaNacimiento, ConstraintValidatorContext context) {
        if (fechaNacimiento == null) {
            return true; // Usá @NotNull aparte si querés que sea obligatorio
        }

        LocalDate nacimiento = new java.sql.Date(fechaNacimiento.getTime()).toLocalDate();
        LocalDate hoy = LocalDate.now();

        return Period.between(nacimiento, hoy).getYears() >= 18;
    }
}