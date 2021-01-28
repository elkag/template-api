package com.template.user.service;

import com.template.user.entities.UserEntity;
import com.template.user.entities.UserPrincipal;
import com.template.user.models.*;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserEntity> getUser(String email);

    UserDTO getUserDTO(String email);

    UserDTO registerUser(RegistrationRequest model);

    UserDTO validateAndLoginFacebookUser(String accessToken);

    PageDTO getAuthors(int pageNumber, int pageSize, String orderBy, String direction);

    PageDTO getAdmins(int pageNumber, int pageSize, String orderBy, String direction);

    List<UserDTO> promoteUsers(List<Long> ids);
    List<UserDTO> demoteUsers(List<Long> ids);

    boolean deleteUser(Long id, boolean isDelete);

    boolean banUser(Long id, boolean isBan);

    UserDTO changePassword(ChangePasswordRequest changePasswordRequest, UserPrincipal principal);
}
