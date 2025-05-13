package org.kevinlin.springbootmall.security;

import org.kevinlin.springbootmall.model.Role;
import org.kevinlin.springbootmall.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.kevinlin.springbootmall.dao.UserDao;


import java.util.ArrayList;
import java.util.List;

@Component
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserDao userDao;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 從資料庫中查詢 Member 數據
        Users user = userDao.getUserByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found for: " + username);

        } else {

            String userEmail = user.getEmail();
            String userPassword = user.getPassword();

            // 權限部分
            List<Role> roleList = userDao.getRolesByUserId(user.getUserId());

            List<GrantedAuthority> authorities = convertToAuthorities(roleList);

            // 轉換成 Spring Security 指定的 User 格式
            return new User(userEmail, userPassword, authorities);
        }
    }

    private List<GrantedAuthority> convertToAuthorities(List<Role> roleList) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roleList) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return authorities;
    }
}
