package org.kevinlin.springbootmall.dao;

import org.kevinlin.springbootmall.model.Role;

public interface RoleDao {

    Role getRoleByName(String roleName);
}

