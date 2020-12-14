package com.template.user.rest;

import com.template.user.model.RegistrationModel;
import com.template.user.service.UserService;
import com.template.config.security.TokenProvider;
import com.template.user.model.UserModel;
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
public class RegistrationController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    public RegistrationController(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/user")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody final RegistrationModel registrationModel) {

        final UserModel registered = userService.registerUser(registrationModel);

        final String jwtToken = tokenProvider.createToken(registered.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Headers", "x-token");
        headers.set("Access-Control-Expose-Headers", "x-token");
        headers.set("x-token", jwtToken);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
