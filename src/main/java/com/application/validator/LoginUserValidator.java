package com.application.validator;

import com.application.dto.LoginUserDTO;
import com.application.entity.User;
import com.application.service.UserService;
import com.application.util.annotation.Inject;

import java.util.Optional;

public class LoginUserValidator implements Validator<LoginUserDTO> {

    @Inject
    private UserService userService;

    @Override
    public ValidationResult validate(LoginUserDTO loginUserDTO) {
        Optional<User> optionalUser = userService.findByEmail(loginUserDTO.getEmail());
        ValidationResult validationResult = new ValidationResult();
        if (optionalUser.isEmpty()) {
            validationResult.add("No such user exists!");
        }
        if (loginUserDTO.getPassword().length() < 6) {
            validationResult.add("Password must be at least 6 characters!");
        }
        if (loginUserDTO.getPassword().length() > 20) {
            validationResult.add("Password must be less than 20 characters!");
        }
        return validationResult;
    }
}
