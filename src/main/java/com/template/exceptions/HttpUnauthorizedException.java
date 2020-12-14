package com.template.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class HttpUnauthorizedException extends RuntimeException {

    public HttpUnauthorizedException(String message) {
        super(message);
    }
}
