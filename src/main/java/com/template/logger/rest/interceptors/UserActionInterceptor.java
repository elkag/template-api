package com.template.logger.rest.interceptors;

import com.template.config.application.properties.AppProperties;
import com.template.logger.service.LogService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class UserActionInterceptor implements HandlerInterceptor {

    private final LogService logService;
    private final AppProperties appProperties;
    public UserActionInterceptor(LogService logService, AppProperties appProperties) {
        this.logService = logService;
        this.appProperties = appProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.getSession(true);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String remoteAddress = request.getRemoteAddr();
        if(request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1")){
            try {
                remoteAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ignored) {

            }
        }
        ActionType actionType = request.getParameter("id") != null ? ActionType.CLICK : ActionType.SEARCH;
        CommonLogData data = CommonLogData.builder()
                .principal(request.getUserPrincipal())
                .remoteAddress(remoteAddress)
                .session( request.getSession(false))
                .actionType(actionType)
                .build();
        if(actionType.equals(ActionType.CLICK)) {
            long itemId = Long.parseLong(request.getParameter("id"));
            logService.logAction(data, itemId);
        } else {
            String search = request.getParameter("t");
            logService.logAction(data, search);
        }
    }
}
