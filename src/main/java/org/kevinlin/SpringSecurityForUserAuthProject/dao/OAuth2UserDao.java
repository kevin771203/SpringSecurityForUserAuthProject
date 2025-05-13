package org.kevinlin.springbootmall.dao;

import org.kevinlin.springbootmall.model.OAuth2User;

public interface OAuth2UserDao {

    OAuth2User getOAuth2User(String provider, String providerId);

    Long createOAuth2User(OAuth2User OAuth2User);
}

