package com.template.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  public ResponseEntity<FileSizeExceedsError> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
                                                                                   final HttpServletRequest request) {
    FileSizeExceedsError error = new FileSizeExceedsError(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds the limit of 5MB", "File size exceeds limit", request);
    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Error> handleBadCredentialsException(BadCredentialsException ex,
                                                             final HttpServletRequest request) {
    Error error = new Error(HttpStatus.UNAUTHORIZED, "Bad Credentials", "Invalid login details", request);
    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Error> handleEntityNotFoundException(EntityNotFoundException ex,
                                                             final HttpServletRequest request) {
    Error error = new Error(HttpStatus.NOT_FOUND, "Requested entity was not found", "Requested entity was not found", request);
    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }

  @ExceptionHandler(HttpUnauthorizedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Error> httpUnauthorizedException(final HttpUnauthorizedException ex,
                                                         final HttpServletRequest request) {

    Error error = new Error(HttpStatus.UNAUTHORIZED, ex.getMessage(), "Unauthorized", request);

    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }

  @ExceptionHandler(HttpForbiddenException.class)
  public ResponseEntity<Error> httpForbiddenException(final HttpServletRequest request) {
    Error error = new Error(HttpStatus.FORBIDDEN, "Forbidden", "Forbidden", request);

    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }

  @ExceptionHandler(HttpBadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Error> handleBadRequestException(HttpBadRequestException ex, final HttpServletRequest request) {
    Error error;
    if(ex.getErrors() == null) {
      error = new Error(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of(ex.getMessage()), request);
    } else {
      error = new Error(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrors(), request);
    }

    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException ex, final HttpServletRequest request) {
    List<String> errors = new ArrayList<>();
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    for (ConstraintViolation<?> violation : violations) {
      errors.add(violation.getMessage());
    }

    Error error = new Error(HttpStatus.BAD_REQUEST, "Bad request", errors, request);
    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }

  @NonNull
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                @NonNull HttpHeaders headers,
                                                                @NonNull HttpStatus status,
                                                                @NonNull WebRequest request) {

    final List<String> errors = new ArrayList<>();
    for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getDefaultMessage());
    }
    for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getDefaultMessage());
    }
    final Error apiError = new Error(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors, request);
    return handleExceptionInternal(ex, apiError, headers, HttpStatus.valueOf(apiError.getStatus()), request);

  }

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<Object> handleAccessDeniedExceptionPost(AccessDeniedException ex,
                                                                final HttpServletRequest request,
                                                                final HttpServletResponse response) {
    Error error = new Error(HttpStatus.UNAUTHORIZED, "Access is denied", ex.getMessage(), request);

    return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
  }
}
