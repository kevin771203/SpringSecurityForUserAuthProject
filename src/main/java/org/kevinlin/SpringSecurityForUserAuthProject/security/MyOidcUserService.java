package org.kevinlin.springbootmall.security;

import org.kevinlin.springbootmall.dao.OAuth2UserDao;
import org.kevinlin.springbootmall.model.OAuth2User;
import org.kevinlin.springbootmall.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

// 用來處理 OAuth 2.0 + OpenID Connect 的社交登入（ex: Google）
@Component
public class MyOidcUserService extends OidcUserService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private OAuth2UserDao oAuth2UserDao;

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(oidcUserRequest);

        // 取得 oidcUser 和 oidcUserRequest 中的資訊
        String email = Objects.toString(oidcUser.getAttributes().get("email"), null);
        String name = Objects.toString(oidcUser.getAttributes().get("name"), null);

        String provider = oidcUserRequest.getClientRegistration().getRegistrationId();
        String providerId = Objects.toString(oidcUser.getAttributes().get("sub"), null);

        String accessToken = oidcUserRequest.getAccessToken().getTokenValue();
        Date expiresAt = Date.from(oidcUserRequest.getAccessToken().getExpiresAt());

        // 從資料庫查詢此 provider + providerId 組合的 oauth2 member 是否存在
        OAuth2User oAuth2User = oAuth2UserDao.getOAuth2User(provider, providerId);

        // 如果 oauth2 member 不存在，就創建一個新的 member
        if (oAuth2User == null) {
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

        // 返回 Spring Security 原本的 oidcUser
        return oidcUser;
    }
}
