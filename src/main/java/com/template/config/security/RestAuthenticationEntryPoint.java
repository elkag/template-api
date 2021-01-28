package com.template.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.exceptions.HttpBadRequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Log4j2
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws HttpBadRequestException, IOException {

        final String invalid = (String) request.getAttribute("invalid");

        if (invalid!=null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String message;
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            } else {
                message = e.getMessage();
            }

            final String invalidMsg = (String) request.getAttribute("invalid");
            message = (invalidMsg != null) ? invalidMsg : message;

            byte[] body = new ObjectMapper()
                    .writeValueAsBytes(
                            Map.of( "status", String.valueOf(response.getStatus()),
                                    "error", "Invalid login details",
                                    "message", message,
                                    "timestamp", Instant.now().toString(),
                                    "path", request.getRequestURI()));
            response.getOutputStream().write(body);
            response.flushBuffer();
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }

    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AccessDeniedException e) throws HttpBadRequestException, IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message;
        if (e.getCause() != null) {
            message = e.getCause().getMessage();
        } else {
            message = e.getMessage();
        }

        final String invalidMsg = (String) request.getAttribute("invalid");
        message = (invalidMsg != null) ? invalidMsg : message;

        byte[] body = new ObjectMapper()
                .writeValueAsBytes(Collections.singletonMap("errors", Map.of( "error", message,"message", "Access is denied. Invalid login details")));
        response.getOutputStream().write(body);
        response.flushBuffer();
    }

}
