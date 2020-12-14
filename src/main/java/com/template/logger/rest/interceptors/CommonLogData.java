package com.template.logger.rest.interceptors;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Builder
@Getter
@Setter
public class CommonLogData {
    private final Principal principal;
    private final HttpSession session;
    private final String remoteAddress;
    private final ActionType actionType;
}
