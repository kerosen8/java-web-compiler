package com.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeDTO {

    private Integer id;
    private String code;
    private String title;

}
