package com.grup14.luterano.utils;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public final class CsvUtils {
    private CsvUtils() {
    }

    public static CSVParser createParser(InputStream in, String charsetOrNull) throws IOException {
        Charset cs = (charsetOrNull != null && !charsetOrNull.isBlank())
                ? Charset.forName(charsetOrNull)
                : Charset.forName("windows-1252");

        Reader reader = new InputStreamReader(in, cs);
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build();
        return new CSVParser(reader, format);
    }

    public static String get(CSVRecord rec, String... canonicalHeaders) {
        for (String want : canonicalHeaders) {
            for (String h : rec.getParser().getHeaderMap().keySet()) {
                if (HeaderAliases.canon(h).equals(HeaderAliases.canon(want))) {
                    String v = rec.get(h);
                    return v == null ? "" : v.trim();
                }
            }
        }
        return "";
    }
}
