package com.template.user.oauth2;

import com.template.exceptions.HttpUnauthorizedException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(SocialAuthProvider authProvider, Map<String, Object> attributes) {

        if(authProvider == SocialAuthProvider.GOOGLE) {
            return new GoogleOAuth2UserInfo(attributes);
        }

        if (authProvider == SocialAuthProvider.FACEBOOK) {
            return new FacebookOAuth2UserInfo(attributes);
        }

        throw new HttpUnauthorizedException("Unauthorized.");
    }
}
