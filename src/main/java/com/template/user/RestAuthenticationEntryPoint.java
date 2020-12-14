package com.template.user;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {
        log.info("Responding with unauthorized error. Message - {}", e.getMessage());

        httpServletResponse.resetBuffer();
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        httpServletResponse.setHeader("Content-Type", "application/json");
        httpServletResponse.getOutputStream().print("{\"error\":\"true\", \"errorMessage\":\"Invalid Login details\"}");
        httpServletResponse.flushBuffer(); //marks response as committed -- if we don't do this the request will go through normally!
    }
}
