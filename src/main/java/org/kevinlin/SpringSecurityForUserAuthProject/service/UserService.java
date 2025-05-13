package org.kevinlin.springbootmall.service;

import org.kevinlin.springbootmall.dto.UserLoginRequest;
import org.kevinlin.springbootmall.dto.UserRegisterRequest;
import org.kevinlin.springbootmall.model.Users;
import org.springframework.security.core.Authentication;

public interface UserService {

    Long register(UserRegisterRequest userRegisterRequest);

    Users getUserById(Long userId);

//    Users login(UserLoginRequest userLoginRequest);
}
