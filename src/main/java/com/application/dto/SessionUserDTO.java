package com.application.dto;

import com.application.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionUserDTO {

    private Integer userId;
    private Role role;

}
