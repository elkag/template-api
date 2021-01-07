package com.template.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  public ResponseEntity<FileSizeExceedsError> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) throws IOException {
    FileSizeExceedsError error = new FileSizeExceedsError(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds the limit of 5MB", "File size exceeds limit");

    return new ResponseEntity<>(error, error.getStatus());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Error> handleEntityNotFoundException(EntityNotFoundException ex) {
    Error error = new Error(HttpStatus.NOT_FOUND, "Requested entity was not found", "Requested entity was not found");

    return new ResponseEntity<>(error, error.getStatus());
  }


  @ExceptionHandler(HttpUnauthorizedException.class)
  public ResponseEntity<Error> httpUnauthorizedException(final HttpServletResponse response) throws IOException {

    Error error = new Error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Unauthorized");

    return new ResponseEntity<>(error, error.getStatus());
  }

  @ExceptionHandler(HttpForbiddenException.class)
  public ResponseEntity<Error> httpForbiddenException(final HttpServletResponse response) throws IOException {
    Error error = new Error(HttpStatus.FORBIDDEN, "Forbidden", "Forbidden");

    return new ResponseEntity<>(error, error.getStatus());
  }

  @ExceptionHandler(HttpBadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Error> handleBadRequestException(HttpBadRequestException ex) {
    Error error;
    if(ex.getErrors() == null) {
      error = new Error(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of(ex.getMessage()));
    } else {
      error = new Error(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrors());
    }

    return new ResponseEntity<>(error, error.getStatus());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException ex) {
    List<String> errors = new ArrayList<>();
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    for (ConstraintViolation<?> violation : violations) {
      errors.add(violation.getMessage());
    }

    Error error = new Error(HttpStatus.BAD_REQUEST, "Bad request", errors);
    return new ResponseEntity<>(error, error.getStatus());
  }
}
