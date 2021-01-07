package com.template.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;


@Getter
public class Error {
    protected final HttpStatus status;
    protected final String message;
    protected final List<String> errors;

    public Error(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public Error(HttpStatus status, String message, String error) {
        this.status = status;
        this.message = message;
        errors = Collections.singletonList(error);
    }
}
