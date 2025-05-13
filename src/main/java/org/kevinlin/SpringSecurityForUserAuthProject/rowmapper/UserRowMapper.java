package org.kevinlin.springbootmall.rowmapper;

import org.kevinlin.springbootmall.model.Users;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<Users> {

    @Override
    public Users mapRow(ResultSet resultSet, int i) throws SQLException {

        Users user = new Users();

        user.setUserId(resultSet.getLong("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setCreatedDate(resultSet.getTimestamp("created_date"));
        user.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));

        return user;
    }
}
