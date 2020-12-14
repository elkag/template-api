package com.template.user.rest;

import com.template.user.model.LoginModel;
import com.template.user.service.UserService;
import com.template.config.security.SecurityConstants;
import com.template.config.security.TokenProvider;
import com.template.user.model.UserModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

   private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public void loginUser(@Valid @RequestBody final LoginModel loginModel, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginModel.getEmail(), loginModel.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwtToken = tokenProvider.createToken(loginModel.getEmail());

        response.addHeader("Access-Control-Allow-Headers", "x-token");
        response.addHeader("Access-Control-Expose-Headers", "x-token");
        response.addHeader("x-token", jwtToken);
    }

    @PostMapping("/facebook-login")
    public ResponseEntity<Void> loginFacebookUser(HttpServletRequest request) {
        final String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Call facebook's API to validate id_token and get user's details
        final UserModel userModel = userService.validateAndLoginFacebookUser(token);

       // final String jwtToken = tokenProvider.createToken(userModel.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Headers", "x-token");
        headers.set("Access-Control-Expose-Headers", "x-token");
       // headers.set("x-token", jwtToken);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public UserModel validateUser(@AuthenticationPrincipal Principal principal) {
        return userService.getUserDTO(principal.getName());
    }
}
