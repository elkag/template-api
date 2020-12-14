package com.template.user.service;


import com.template.user.entities.UserEntity;
import com.template.user.model.RegistrationModel;
import com.template.user.model.UserModel;

import java.util.Optional;

public interface UserService {

    Optional<UserEntity> getUser(String email);

    UserModel getUserDTO(String email);

    UserModel registerUser(RegistrationModel model);

    UserModel validateAndLoginFacebookUser(String accessToken);
}
