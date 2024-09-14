package com.application.dto;

import com.application.entity.Role;
import lombok.*;

@Setter
@Getter
@Data
@Builder
public class CreateUserDTO {

    private String email;
    private String password;
    private Role role;

}

