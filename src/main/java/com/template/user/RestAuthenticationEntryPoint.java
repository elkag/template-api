package com.template.user;

import com.template.exceptions.Error;
import com.template.exceptions.HttpBadRequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws HttpBadRequestException, IOException {
        log.info("Responding with unauthorized error. Message - {}", e.getMessage());
        //resolver.resolveException(request, response, null, e);
        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().print("{\"error\":\"Forbidden\", \"message\":\"Invalid Login details\"}");
        response.flushBuffer(); //marks response as committed -- if we don't do this the request will go through normally!
    }
}
