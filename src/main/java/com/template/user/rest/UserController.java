package com.template.user.rest;

import com.template.item.models.ValuesAllowed;
import com.template.user.entities.UserPrincipal;
import com.template.user.mappers.UserMapper;
import com.template.user.models.*;
import com.template.user.service.UserService;
import com.template.config.security.SecurityConstants;
import com.template.config.security.TokenProvider;
import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@Api(value="users", tags = {"Users controller"})
@Validated
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @ApiOperation(value = "Login user",
            notes = "This method is used to login user")
    @PostMapping("/login")
    public void loginUser(@Valid @RequestBody final LoginModel loginModel, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginModel.getEmail(), loginModel.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwtToken = tokenProvider.createToken(loginModel.getEmail());

        response.addHeader("Access-Control-Allow-Headers", "x-token");
        response.addHeader("Access-Control-Expose-Headers", "x-token");
        response.addHeader("x-token", jwtToken);
        response.setContentType(MediaType.APPLICATION_JSON.getType());
    }

    @ApiOperation(value = "Login Facebook user.")
    @PostMapping("/facebook-login")
    public ResponseEntity<Void> loginFacebookUser(HttpServletRequest request) {
        final String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Call facebook's API to validate id_token and get user's details
        final UserDTO userDto = userService.validateAndLoginFacebookUser(token);

        final String jwtToken = tokenProvider.createToken(userDto.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Headers", "x-token");
        headers.set("Access-Control-Expose-Headers", "x-token");
        headers.set("x-token", jwtToken);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @ApiOperation(value = "This method is used to validate JWT token.", authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/validate")
    public ResponseEntity<UserDTO> validate(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(UserMapper.INSTANCE.toModel(principal.getUserEntity()));
    }

    @ApiOperation(value = "Change user's password",
            notes = "This method is used to change user's password",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('AUTHOR')")
    @PutMapping("/change-password")
    public ResponseEntity<UserDTO> changePassword(
            @Valid
            @RequestBody
            @ApiParam(
                    name =  "changePasswordRequest",
                    type = "Object",
                    value = "Change password request model",
                    required = true) final ChangePasswordRequest changePasswordRequest,
            @AuthenticationPrincipal UserPrincipal principal) {

        final UserDTO updated = userService.changePassword(changePasswordRequest, principal);

        final String jwtToken = tokenProvider.createToken(updated.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Headers", "x-token");
        headers.set("Access-Control-Expose-Headers", "x-token");
        headers.set("x-token", jwtToken);

        return new ResponseEntity<>(updated, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "Get authors (pageable)",
            notes = "Get a list of users have \"Author\" role but not \"Admin\" role",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/authors")
    public ResponseEntity<PageDTO> getAuthors(
            @RequestParam("p")
            @NotNull(message = "Page number (p) must not be null.")
            @Min(value = 0, message="Page number (p) must be equal or greater than 0.")
            @ApiParam(
                    name =  "p",
                    type = "Integer",
                    value = "Page number.\n" +
                            "Starts from 0 - [0, 1, ..., n]",
                    example = "1",
                    required = true
            ) int page,
            @RequestParam("s")
            @NotNull(message = "Content size (s) must not be null.")
            @Min(value = 1, message="Content size (s) must be equal or greater than 1.")
            @ApiParam(
                    name =  "s",
                    type = "Integer",
                    value = "Count of the rows in the page",
                    example = "10",
                    required = true
            ) int size,
            @RequestParam(value = "order", required = false)
            @ValuesAllowed(propName = "order",
                    values = {
                        "username",
                        "name",
                        "date"
                    })
            @ApiParam(
                    name =  "order",
                    type = "String",
                    value = "Order could be by user's full name, username or registration date:",
                    example = "username",
                    allowableValues = "username, name, date",
                    defaultValue = "date") String order,
            @RequestParam(value = "dir", required = false)
            @ValuesAllowed(propName = "dir",
                    values = {
                        "asc",
                        "desc"
                    })
            @ApiParam(
                    name =  "dir",
                    type = "String",
                    value = "Order direction (ASC or DESC):",
                    example = "asc",
                    allowableValues = "asc, desc",
                    defaultValue = "desc") String  direction) {
        return ResponseEntity.ok(userService.getAuthors(page, size, order, direction));
    }

    @ApiOperation(value = "Get admins (pageable)",
            notes = "Get a list of users have \"Admin\" role (except the root admin)",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/admins")
    public ResponseEntity<PageDTO> getAdmins(
            @RequestParam("p")
            @NotNull(message = "Page number (p) must not be null.")
            @Min(value = 0, message="Page number (p) must be equal or greater than 0.")
            @ApiParam(
                    name =  "p",
                    type = "Integer",
                    value = "Page number.\n" +
                            "Starts from 0 - [0, 1, ..., n]",
                    example = "1",
                    required = true
            ) int page,
            @RequestParam("s")
            @NotNull(message = "Content size (s) must not be null.")
            @Min(value = 1, message="Content size (s) must be equal or greater than 1.")
            @ApiParam(
                    name =  "s",
                    type = "Integer",
                    value = "Count of the rows in the page",
                    example = "10",
                    required = true
            ) int size,
            @RequestParam(value = "order", defaultValue = "date", required = false)
            @ApiParam(
                    name =  "order",
                    type = "String",
                    value = "Order could be by user's full name, username or registration date:",
                    example = "username",
                    allowableValues = "username, name, date")
            @ValuesAllowed(propName = "order",
                    values = {
                        "username",
                        "name",
                        "date"
                    }) String order,
            @RequestParam(value = "dir", defaultValue = "desc", required = false)
            @ApiParam(
                    name =  "dir",
                    type = "String",
                    value = "Order direction (ASC or DESC):",
                    example = "asc",
                    allowableValues = "asc, desc")
            @ValuesAllowed(propName = "dir",
                    values = {
                        "asc",
                        "desc"
                    }) String  direction) {
        return ResponseEntity.ok(userService.getAdmins(page, size, order, direction));
    }

    @ApiOperation (value = "Promote \"Authors\" to \"Admins\"",
            notes = "Promote users with Author roles to Admins",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/promote")
    public ResponseEntity<List<UserDTO>> promote(
            @RequestBody
            @Valid
            @NotEmpty
            @ApiParam(
                name = "ids",
                type = "List",
                value = "List of demoted user's id") List<@Valid Long> ids) {
        List<UserDTO> response = userService.promoteUsers(ids);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Demote \"Admins\" to \"Authors\"",
            notes = "Demote users with Admin roles to Authors",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/demote")
    public ResponseEntity<List<UserDTO>> demote(
            @RequestBody
            @Valid
            @NotEmpty
            @ApiParam(
                name = "ids",
                type = "List",
                value = "List of demoted user's id") List<@Valid Long> ids) {

        List<UserDTO> response = userService.demoteUsers(ids);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "This method is used to ban or unban an user",
            notes = "Ban/unban user",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/ban")
    public ResponseEntity<?> banUser(
            @RequestParam
            @ApiParam(
                    name =  "id",
                    type = "integer",
                    value = "User's id")
            @Valid final Long id,
            @RequestParam
            @ApiParam(
                    name =  "ban",
                    type = "boolean",
                    value = "Is banned",
                    example = "true",
                    allowableValues = "true, false")
            @Valid final boolean ban) {
        userService.banUser(id, ban);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "This method is used to delete or recover user's account",
            notes = "Delete/recover account",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(
            @RequestParam
            @ApiParam(
                    name =  "id",
                    type = "integer",
                    value = "User's id")
            @Valid final Long id,
            @RequestParam
            @ApiParam(
                    name =  "delete",
                    type = "boolean",
                    value = "To delete or recover account",
                    example = "true",
                    allowableValues = "true, false")
            @Valid final boolean delete) {
        userService.deleteUser(id, delete);
        return ResponseEntity.ok().build();
    }
}