package com.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCodeDTO {

    private Integer userId;
    private String code;
    private String path;
    private String title;

}
