package com.application.validator;

import com.application.dto.CreateUserDTO;
import com.application.service.UserService;
import com.application.util.annotation.Inject;

public class CreateUserValidator implements Validator<CreateUserDTO> {

    @Inject
    private UserService userService;

    @Override
    public ValidationResult validate(CreateUserDTO createUserDTO) {
        ValidationResult validationResult = new ValidationResult();
        if (userService.findUserByEmail(createUserDTO.getEmail()).isPresent()) {
            validationResult.add("This email is already taken!");
        }
        if (createUserDTO.getPassword().length() < 6) {
            validationResult.add("Password must be at least 6 characters!");
        }
        if (createUserDTO.getPassword().length() > 20) {
            validationResult.add("Password must be less than 20 characters!");
        }
        if (createUserDTO.getPassword().chars().mapToObj(c -> (char) c).noneMatch(Character::isUpperCase)) {
            validationResult.add("Password must contain uppercase letters!");
        }
        return validationResult;
    }
}
