package org.kevinlin.springbootmall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class MySecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // 設定 Session 的創建機制
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                // 設定 CSRF 保護
//                .csrf(csrf -> csrf.disable())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(createCsrfHandler())
                        .ignoringRequestMatchers("/userRegister", "/userLogin")
                )

                // 設定 CORS 跨域
                .cors(cors -> cors
                        .configurationSource(createCorsConfig())
                )

                // 添加客製化的 Filter
                .addFilterBefore(new MyFilter(), BasicAuthenticationFilter.class)

                .httpBasic(Customizer.withDefaults())

                // 設定 Form 登入
                .formLogin(form -> form
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/userLogin", true)
                )


                // 設定 OAuth 2.0 社交登入
//                .oauth2Login(Customizer.withDefaults())

                .authorizeHttpRequests(request -> request
                        // 註冊與登入功能開放
                        .requestMatchers("/userRegister").permitAll()
                        .requestMatchers("/userLogin").authenticated()

                        // 一般會員可以查詢商品與下訂單
                        .requestMatchers("/products", "/products/{productId}", "/users/{userId}/orders")
                            .hasAnyRole("NORMAL_MEMBER", "ADMIN")  // 👈 合併權限

                        // 管理者才可以操作商品資料
                        .requestMatchers("/products/**","/v3/api-docs").hasRole("ADMIN")

                        // 其他請求一律禁止
                        .anyRequest().denyAll()
                )

                .build();
    }


    private CsrfTokenRequestAttributeHandler createCsrfHandler() {
        return new CsrfTokenRequestAttributeHandler(); // 使用預設值
    }

    private CorsConfigurationSource createCorsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://example.com"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
