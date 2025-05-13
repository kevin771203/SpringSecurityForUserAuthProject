package org.kevinlin.springbootmall.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

public class MyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.equals("/userLogin")) {
            String userAgent = request.getHeader("User-Agent");
            Date now = new Date();

            System.out.println("使用者於 " + now + " 嘗試從 " + userAgent + " 登入");
        }

        // 把 request 和 response 繼續往下傳遞
        filterChain.doFilter(request, response);
    }
}
