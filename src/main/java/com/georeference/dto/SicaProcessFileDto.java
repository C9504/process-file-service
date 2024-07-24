package com.georeference.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SicaProcessFileDto {
    private String id;
    private String fileName;
    private String content;
}
