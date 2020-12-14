package com.template.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.security.oauth2.provider.facebook")
@Configuration("facebook")
@Data
public class SocialLoginProperties {

    private String userInfoUri;

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    @Override
    public String toString() {
        return "SocialLoginProperties{" +
                "userInfoUri='" + userInfoUri + '\'' +
                '}';
    }
}
