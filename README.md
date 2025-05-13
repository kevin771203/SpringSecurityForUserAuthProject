## 📌 專案簡介

本專案是使用 Spring Boot 與 Spring Security 架構，實作常見的使用者登入與權限驗證流程。透過 JWT 、Session 或基本表單登入，達成以下功能：

- 使用者註冊與登入
- 密碼加密（BCrypt）
- 基於角色的授權（RBAC）
- API 保護與未授權處理
- CORS 跨域處理
- CSRF 安全處理
- OAuth2.0 社交登入
- Filter 監看登入紀錄

---

## 🧰 技術棧

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Web / Spring JDBC**
- **MySQL**（用於儲存使用者資訊）
- **Maven 构建**

---

## 🛡️ 使用者驗證與授權流程

1. 使用者透過 `/login` 發送帳密
2. 系統透過 `UserDetailsService` 查詢帳號並驗證密碼（BCrypt 編碼比對）
3. 登入成功後產生 Session 或 JWT Token（視實作而定）
4. 後續 API 請求將會被 `SecurityFilterChain` 判斷是否有存取權限

---

## 🔐 Spring Security 設定重點

```java
@Configuration
@EnableWebSecurity
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
````

---

## 🔐 密碼加密（BCrypt）

使用 Spring 提供的密碼加密器，確保密碼安全儲存：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## 🧪 API 測試範例

### 1️⃣ 使用者登入

```http
POST /login
Content-Type: application/x-www-form-urlencoded

username=kevin&password=123456
```

成功登入後會建立 Session 或回傳 Token（若有設定）。

### 2️⃣ 保護資源存取

```http
GET /admin/dashboard
Authorization: Bearer <JWT Token>
```

若無權限，回傳 HTTP 403 Forbidden。

---

## 🚀Spring Security 安全性設定說明
此專案使用 Spring Security，整合以下功能：

✅ Session 管理

✅ CSRF 防護（支援 Cookie 傳遞 Token）

✅ CORS 跨來源設定

✅ 表單登入（/login）

✅ 角色權限控管（一般會員、管理員）

✅ 自訂 Filter (監看使用者登入紀錄)

✅ OAuth2 第三方社交登入（可擴充支援 LINE / Facebook / Google /Github）



---

## 🚀 啟動方式

1. 確保你本機或 Docker 有 MySQL 資料庫運行
2. 設定 `application.yml` 或 `application.properties` 資料庫資訊
3. 啟動專案

```bash
./mvnw spring-boot:run
```

---
```
