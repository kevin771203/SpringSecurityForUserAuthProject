package org.kevinlin.springbootmall.rowmapper;

import org.kevinlin.springbootmall.model.OAuth2User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OAuth2UserRowMapper implements RowMapper<OAuth2User> {

    @Override
    public OAuth2User mapRow(ResultSet resultSet, int i) throws SQLException {

        OAuth2User OAuth2User = new OAuth2User();

        OAuth2User.setOauth2UserId(resultSet.getLong("oauth2_user_id"));
        OAuth2User.setProvider(resultSet.getString("provider"));
        OAuth2User.setProviderId(resultSet.getString("provider_id"));
        OAuth2User.setName(resultSet.getString("name"));
        OAuth2User.setEmail(resultSet.getString("email"));
        OAuth2User.setAccessToken(resultSet.getString("access_token"));
        OAuth2User.setExpiresAt(resultSet.getTimestamp("expires_at"));

        return OAuth2User;
    }
}
