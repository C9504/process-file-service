package com.georeference.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {

    private String fileName;
    private String fileBody;
}
