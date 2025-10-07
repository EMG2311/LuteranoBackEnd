package com.grup14.luterano.response.imports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportResultResponse {
    private int totalRows;
    private int inserted;
    private int updated;
    private int reactivated;
    private int skipped;
    private List<String> errors = new ArrayList<>();
}