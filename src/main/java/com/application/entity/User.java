package com.application.entity;

import lombok.*;

@Setter
@Getter
@Data
@Builder
public class User {

    private Integer id;
    private String email;
    private String password;
    private Role role;

}
