package com.template.user.service.impl;

import com.template.user.entities.Authority;
import com.template.user.mappers.RegistrationMapper;
import com.template.user.model.RegistrationModel;
import com.template.user.oauth2.OAuth2UserInfo;
import com.template.user.oauth2.OAuth2UserInfoFactory;
import com.template.user.oauth2.SocialAuthProvider;
import com.template.user.service.UserService;
import com.template.config.SocialLoginProperties;
import com.template.exceptions.HttpBadRequestException;
import com.template.user.mappers.UserMapper;
import com.template.user.entities.AuthorityEntity;
import com.template.user.entities.UserEntity;
import com.template.user.entities.UserRepository;
import com.template.user.model.UserModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SocialLoginProperties socialLoginProperties;

    private final RestTemplate restTemplate;

    public UserServiceImpl(UserRepository userRepository, SocialLoginProperties socialLoginProperties, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.socialLoginProperties = socialLoginProperties;
        this.restTemplate = restTemplate;
    }

    private UserModel getOrCreateFacebookUser(OAuth2UserInfo oAuth2UserInfo) {

        Optional<UserEntity> userEntityOpt =
                userRepository.findOneByUsername(oAuth2UserInfo.getEmail());

        final UserEntity user =  userEntityOpt.
                orElseGet(() -> createFacebookUser(oAuth2UserInfo));

        final UserEntity updated = updateUser(user, oAuth2UserInfo);

        return UserMapper.INSTANCE.toModel(updated);
    }

    private UserEntity updateUser(UserEntity userEntity, OAuth2UserInfo facebookInfo) {
        userEntity.setImage(facebookInfo.getImageUrl());
        userEntity.setFirstName(facebookInfo.getFirstName());
        userEntity.setLastName(facebookInfo.getLastName());

        return userRepository.save(userEntity);
    }

    private UserEntity createFacebookUser(OAuth2UserInfo userInfo) {
        log.info("Creating a new user with email [PROTECTED].");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userInfo.getEmail());
        userEntity.setFirstName(userInfo.getFirstName());
        userEntity.setLastName(userInfo.getLastName());

        AuthorityEntity userRoleAuthor = new AuthorityEntity().setRole(Authority.USER.name());
        AuthorityEntity userRoleUser = new AuthorityEntity().setRole(Authority.AUTHOR.name());
        userEntity.setRoles(List.of(userRoleUser, userRoleAuthor));

        return userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> getUser(String email) {
       return userRepository
                .findOneByUsername(email);
    }

    @Override
    public UserModel getUserDTO(final String email) {
        final Optional<UserEntity> userOpt = userRepository
                .findOneByUsername(email);

        return UserMapper.INSTANCE.toModel(userOpt.orElse(null));
    }

    @Override
    public UserModel registerUser(final RegistrationModel registrationModel) {
        log.info("Create user BEGIN: {}", registrationModel.getEmail());

        final UserEntity user = RegistrationMapper.INSTANCE.toUser(registrationModel);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        if(!violations.isEmpty()) {
            throw new HttpBadRequestException(violations.stream().findFirst().get().getMessage());
        }

        final Optional<UserEntity> userOpt = userRepository
                .findOneByUsername(user.getUsername());

        if(userOpt.isPresent()) {
            throw new HttpBadRequestException(String.format("Email {%s} already exists", user.getUsername()));
        }

        AuthorityEntity userRoleAuthor = new AuthorityEntity().setRole(Authority.USER.name());
        AuthorityEntity userRoleUser = new AuthorityEntity().setRole(Authority.AUTHOR.name());
        user.setRoles(List.of(userRoleUser, userRoleAuthor));

        final UserEntity saved = userRepository.save(user);

        log.info("Create user END: {}", saved);

        return UserMapper.INSTANCE.toModel(saved);
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserModel validateAndLoginFacebookUser(String accessToken) {

       Map<String, Object> attributes = null;

        final String fields = "id,email,first_name,last_name,picture";
        try {

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(socialLoginProperties.getUserInfoUri())
                    .queryParam("access_token", accessToken).queryParam("fields", fields);

            attributes = restTemplate.getForObject(uriBuilder.toUriString(), Map.class);

            OAuth2UserInfo oAuth2UserInfo =
                    OAuth2UserInfoFactory.getOAuth2UserInfo(SocialAuthProvider.FACEBOOK, attributes);

            return getOrCreateFacebookUser(oAuth2UserInfo);

        } catch (HttpClientErrorException e) {
            throw new HttpBadRequestException("Invalid access token");
        } catch (Exception exp) {
            throw new HttpBadRequestException(exp.getMessage());
        }
    }
}
