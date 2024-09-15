package com.application.dto;

import lombok.*;

@Data
@Builder
public class LoginUserDTO {

    private String email;
    private String password;

}
