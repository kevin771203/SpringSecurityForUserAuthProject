package org.kevinlin.springbootmall.service.impl;

import org.kevinlin.springbootmall.dao.RoleDao;
import org.kevinlin.springbootmall.dao.UserDao;
import org.kevinlin.springbootmall.dto.UserLoginRequest;
import org.kevinlin.springbootmall.dto.UserRegisterRequest;
import org.kevinlin.springbootmall.model.Role;
import org.kevinlin.springbootmall.model.Users;
import org.kevinlin.springbootmall.service.UserService;
import org.kevinlin.springbootmall.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Component
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;




    @Override
    public Long register(UserRegisterRequest userRegisterRequest) {

        //檢查 email 是否存在
        Users existingUser = userDao.getUserByEmail(userRegisterRequest.getEmail());

        if (existingUser != null) {
            log.warn("User with email {} already exists", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }



        //使用 BCrypt 加密演算法將密碼加密
        String hashedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
        userRegisterRequest.setPassword(hashedPassword);
        
        Users user = new Users();
        user.setUserId(idGenerator.generateId());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(userRegisterRequest.getPassword());

        // 為 Member 添加預設的 Role
        Role normalRole = roleDao.getRoleByName("ROLE_NORMAL_MEMBER");
        userDao.addRoleForUserId(user.getUserId(), normalRole);

        //創建 user
        return userDao.createUser(user);
    }

    @Override
    public Users getUserById(Long userId) {
        return userDao.getUserById(userId);
    }

//    @Override
//    public Users login(UserLoginRequest userLoginRequest) {
//
//        try {
//        // 建立驗證用的 token
//        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
//                userLoginRequest.getEmail(),
//                userLoginRequest.getPassword()
//        );
//
//        // 驗證帳密（自動呼叫 UserDetailsService 與 PasswordEncoder）
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//
//        // 驗證成功，回傳資料
//        return userDao.getUserByEmail(authentication.getName());
//
//    } catch (BadCredentialsException e) {
//        log.warn("Invalid login for email {}", userLoginRequest.getEmail());
//        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
//    }
//    }
}
