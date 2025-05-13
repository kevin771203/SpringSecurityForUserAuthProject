package org.kevinlin.springbootmall.security;

import org.kevinlin.springbootmall.dao.OAuth2UserDao;
import org.kevinlin.springbootmall.model.OAuth2User;
import org.kevinlin.springbootmall.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

// 用來處理 OAuth 2.0 的社交登入（ex: GitHub、Facebook、LINE）
@Component
public class MyOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private OAuth2UserDao oAuth2UserDao;

    @Override
    public org.springframework.security.oauth2.core.user.OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        org.springframework.security.oauth2.core.user.OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        // 取得 oAuth2User 和 oAuth2UserRequest 中的資訊
        String email = Objects.toString(oAuth2User.getAttributes().get("email"), null);
        String name = Objects.toString(oAuth2User.getAttributes().get("name"), null);

        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getName();

        String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
        Date expiresAt = Date.from(oAuth2UserRequest.getAccessToken().getExpiresAt());

        // 從資料庫查詢此 provider + providerId 組合的 oauth2 member 是否存在
        OAuth2User oAuth2Member = oAuth2UserDao.getOAuth2User(provider, providerId);

        // 如果 oauth2 member 不存在，就創建一個新的 member
        if (oAuth2Member == null) {
            OAuth2User newOAuth2User = new OAuth2User();
            newOAuth2User.setOauth2UserId(idGenerator.generateId());
            newOAuth2User.setProvider(provider);
            newOAuth2User.setProviderId(providerId);
            newOAuth2User.setName(name);
            newOAuth2User.setEmail(email);
            newOAuth2User.setAccessToken(accessToken);
            newOAuth2User.setExpiresAt(expiresAt);

            oAuth2UserDao.createOAuth2User(newOAuth2User);
        }

        // 返回 Spring Security 原本的 oAuth2User
        return oAuth2User;
    }
}