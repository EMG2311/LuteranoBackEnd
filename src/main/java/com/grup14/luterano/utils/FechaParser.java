package com.grup14.luterano.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public final class FechaParser {
    private FechaParser() {}
    private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static Date parseToDate(String s) {
        if (s == null || s.isBlank()) return null;
        LocalDate ld;
        try { ld = LocalDate.parse(s, DDMMYYYY); }
        catch (DateTimeParseException e) {
            ld = LocalDate.parse(s); // intenta ISO yyyy-MM-dd
        }
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
