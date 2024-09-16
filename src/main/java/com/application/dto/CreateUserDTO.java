package com.application.dto;

import lombok.*;

@Data
@Builder
public class CreateUserDTO {

    private String email;
    private String password;
    private String salt;

}

