package com.georeference.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
}
