package org.kevinlin.springbootmall.controller;

import jakarta.validation.Valid;
import org.kevinlin.springbootmall.dto.UserLoginRequest;
import org.kevinlin.springbootmall.dto.UserRegisterRequest;
import org.kevinlin.springbootmall.model.Users;
import org.kevinlin.springbootmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/userRegister")
    public ResponseEntity<Users> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        Long userId = userService.register(userRegisterRequest);

        Users user = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/userLogin")
    public String login(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return "登入成功！帳號 " + username + " 的權限為: " + authorities;
    }
}
