package org.kevinlin.springbootmall.dao;

import org.kevinlin.springbootmall.model.Role;
import org.kevinlin.springbootmall.model.Users;

import java.util.List;

public interface UserDao {

    Long createUser(Users user);

    Users getUserById(Long userId);

    Users getUserByEmail(String email);

    List<Role> getRolesByUserId(Long userId);

    void addRoleForUserId(Long userId, Role role);
}
