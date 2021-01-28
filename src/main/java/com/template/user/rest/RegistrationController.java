package com.template.user.rest;

import com.template.user.models.RegistrationRequest;
import com.template.user.models.UserDTO;
import com.template.user.service.UserService;
import com.template.config.security.TokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/register")
@Api(value="users", tags = {"Registration controller"})
public class RegistrationController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    public RegistrationController(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @ApiOperation(value = "Register a new user")
    @PostMapping("/user")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody final RegistrationRequest registrationRequest) {

        final UserDTO registered = userService.registerUser(registrationRequest);

        final String jwtToken = tokenProvider.createToken(registered.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Headers", "x-token");
        headers.set("Access-Control-Expose-Headers", "x-token");
        headers.set("x-token", jwtToken);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
