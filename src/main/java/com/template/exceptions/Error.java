package com.template.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Collections;
import java.util.List;


@Getter
public class Error {
    protected final Integer status;
    protected final String message;
    protected final List<String> errors;
    private final String timestamp;
    private final String path;

    public Error(HttpStatus status, String message, List<String> errors, HttpServletRequest request) {
        this.status = status.value();
        this.message = message;
        this.errors = errors;
        timestamp = Instant.now().toString();
        path = request.getRequestURI();
    }

    public Error(HttpStatus status, String message, String error, HttpServletRequest request) {
        this.status = status.value();
        this.message = message;
        errors = Collections.singletonList(error);
        timestamp = Instant.now().toString();
        path = request.getRequestURI();
    }

    public Error(HttpStatus status, String message, List<String> errors, WebRequest request) {
        this.status = status.value();
        this.message = message;
        this.errors = errors;
        timestamp = Instant.now().toString();
        path = ((ServletWebRequest)request).getRequest().getRequestURI();
    }
}
