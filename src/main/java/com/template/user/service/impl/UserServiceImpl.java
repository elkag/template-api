package com.template.user.service.impl;

import com.template.config.security.PasswordEncoder;
import com.template.search.utils.OrderDirection;
import com.template.search.utils.UsersOrder;
import com.template.user.entities.*;
import com.template.user.mappers.RegistrationMapper;
import com.template.user.models.*;
import com.template.user.oauth2.OAuth2UserInfo;
import com.template.user.oauth2.OAuth2UserInfoFactory;
import com.template.user.oauth2.SocialAuthProvider;
import com.template.user.service.UserService;
import com.template.config.application.properties.SocialLoginProperties;
import com.template.exceptions.HttpBadRequestException;
import com.template.user.mappers.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SocialLoginProperties socialLoginProperties;
    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    public UserServiceImpl(UserRepository userRepository, SocialLoginProperties socialLoginProperties, PasswordEncoder passwordEncoder, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.socialLoginProperties = socialLoginProperties;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    private UserDTO getOrCreateFacebookUser(OAuth2UserInfo oAuth2UserInfo) {

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

        AuthorityEntity userRoleAuthor = new AuthorityEntity().setRole(Authority.AUTHOR.name());
        AuthorityEntity userRoleUser = new AuthorityEntity().setRole(Authority.USER.name());
        userEntity.setRoles(userRoleUser, userRoleAuthor);

        return userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> getUser(String email) {
       return userRepository
                .findOneByUsername(email);
    }

    @Override
    public UserDTO getUserDTO(final String email) {
        final Optional<UserEntity> userOpt = userRepository
                .findOneByUsername(email);

        return UserMapper.INSTANCE.toModel(userOpt.orElse(null));
    }

    @Override
    public UserDTO registerUser(final RegistrationRequest registrationRequest) {
        log.info("Create user BEGIN: {}", registrationRequest.getEmail());

        final UserEntity user = RegistrationMapper.INSTANCE.toUser(registrationRequest);

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
        user.setRoles(userRoleUser, userRoleAuthor);

        final UserEntity saved = userRepository.save(user);

        log.info("Create user END: {}", saved);

        return UserMapper.INSTANCE.toModel(saved);
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserDTO validateAndLoginFacebookUser(String accessToken) {

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

    @Override
    public PageDTO getAuthors(int pageNumber, int pageSize, String orderBy, String direction) {

        if(orderBy == null) {
            orderBy = UsersOrder.DATE.name();
            direction = OrderDirection.DESC.name();
        } else {
            orderBy = orderBy.toUpperCase();
            direction = direction.toUpperCase();
        }
        Pageable pageable;
        if(direction.equals(OrderDirection.ASC.name())) {
            if(orderBy.equals(UsersOrder.NAME.name())) {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by("firstName").and(Sort.by("lastName").and(Sort.by("id"))));
            } else {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(UsersOrder.valueOf(orderBy).getOrderBy()).ascending().and(Sort.by("id")));
            }
        } else {
            if(orderBy.equals(UsersOrder.NAME.name())) {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by("firstName").descending().and(Sort.by("lastName").descending().and(Sort.by("id")).descending()));
            } else {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(UsersOrder.valueOf(orderBy).getOrderBy()).descending().and(Sort.by("id")).descending());
            }
        }
        Page<UserEntity> page = userRepository.fetchAuthors(pageable);

        return PageDTO.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .result(UserMapper.INSTANCE.toUserDTOs(page.stream().collect(Collectors.toList())))
                .build();
    }

    @Override
    public PageDTO getAdmins(int pageNumber, int pageSize, String orderBy, String direction) {

        if(orderBy == null) {
            orderBy = UsersOrder.DATE.name();
            direction = OrderDirection.DESC.name();
        } else {
            orderBy = orderBy.toUpperCase();
            direction = direction.toUpperCase();
        }
        Pageable pageable;
        if(direction.equals(OrderDirection.ASC.name())) {
            if(orderBy.equals(UsersOrder.NAME.name())) {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by("firstName").and(Sort.by("lastName").and(Sort.by("id"))));
            } else {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(UsersOrder.valueOf(orderBy).getOrderBy()).ascending().and(Sort.by("id")));
            }
        } else {
            if(orderBy.equals(UsersOrder.NAME.name())) {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by("firstName").descending().and(Sort.by("lastName").descending().and(Sort.by("id")).descending()));
            } else {
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(UsersOrder.valueOf(orderBy).getOrderBy()).descending().and(Sort.by("id")).descending());
            }
        }
        Page<UserEntity> page = userRepository.fetchAdmins(pageable);

        return PageDTO.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .result(UserMapper.INSTANCE.toUserDTOs(page.stream().collect(Collectors.toList())))
                .build();
    }

    @Transactional
    @Override
    public boolean deleteUser(Long id, boolean isDelete) {
        log.info(String.format("Delete user BEGIN: {} -> %d", id));
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if(userOpt.isEmpty()){
            throw new EntityNotFoundException("User entity not found.");
        }
        UserEntity user = userOpt.get();
        boolean isSuperAdmin = user.getRoles().stream().anyMatch(r -> r.getRole().equals(Authority.SUPER_ADMIN.name()));
        if(isSuperAdmin) {
            throw new UnsupportedOperationException("Root admin can not be deleted.");
        }
        user.setDeleted(isDelete);
        userRepository.save(user);
        log.info(String.format("Delete item END: {} -> %d", id));
        return true;
    }

    @Transactional
    @Override
    public boolean banUser(Long id, boolean isBan) {
        log.info(String.format("Ban user BEGIN: {} -> %d", id));
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if(userOpt.isEmpty()){
            throw new EntityNotFoundException("User entity not found.");
        }
        UserEntity user = userOpt.get();
        user.setBanned(isBan);
        userRepository.save(user);
        log.info(String.format("Ban item END: {} -> %d", id));
        return true;
    }

    @Override
    public UserDTO changePassword(ChangePasswordRequest changePasswordRequest, UserPrincipal principal) {
        log.info(String.format("Change password BEGIN: {} -> %d", principal.getUserEntity().getId()));

        final Optional<UserEntity> userOpt = userRepository
                .findOneByUsername(principal.getUserEntity().getUsername());

        if(userOpt.isEmpty()) {
            throw new HttpBadRequestException(String.format("Email {%s} does not exists", principal.getUserEntity().getUsername()));
        }
        UserEntity user = userOpt.get();

        try {
            if(!passwordEncoder.matches(changePasswordRequest.getPassword(), user.getPassword())) {
                throw new HttpBadRequestException("Password does not match");
            }
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequestException("Password does not match");
        }

        user.setPassword(PasswordEncoder.hashPassword(changePasswordRequest.getNewPassword()));

        final UserEntity saved = userRepository.save(user);

        log.info("Change password END: {}", saved);

        return UserMapper.INSTANCE.toModel(saved);
    }

    @Transactional
    @Override
    public List<UserDTO> promoteUsers(List<Long> ids) {

        ids.forEach(i -> {
            Optional<UserEntity> userOpt = userRepository.findById(i);
            if(userOpt.isEmpty()) {
                throw new EntityNotFoundException();
            }
            UserEntity user = userOpt.get();
            AuthorityEntity userRoleAdmin = new AuthorityEntity().setRole(Authority.ADMIN.name());
            UserEntity updated = user.addRole(userRoleAdmin);
            UserEntity saved = userRepository.save(updated);
        });

        List<UserEntity> users = userRepository.findAllById(ids);

        return UserMapper.INSTANCE.toUserDTOs(users);
    }

    @Transactional
    @Override
    public List<UserDTO> demoteUsers(List<Long> ids) {

        userRepository.setDemoted(ids);

        List<UserEntity> users = userRepository.findAllById(ids);

        return UserMapper.INSTANCE.toUserDTOs(users);
    }

    @Transactional
    public void promote(Set<Long> promoted) {
        promoted.forEach(i -> {
            Optional<UserEntity> userOpt = userRepository.findById(i);
            if(userOpt.isEmpty()) {
                throw new EntityNotFoundException();
            }
            UserEntity user = userOpt.get();
            AuthorityEntity userRoleAdmin = new AuthorityEntity().setRole(Authority.ADMIN.name());
            UserEntity updated = user.addRole(userRoleAdmin);
            UserEntity saved = userRepository.save(updated);
        });
    }
}
