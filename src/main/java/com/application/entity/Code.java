package com.application.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class Code {

    private Integer id;
    private Integer userId;
    private String path;
    private String title;

}
