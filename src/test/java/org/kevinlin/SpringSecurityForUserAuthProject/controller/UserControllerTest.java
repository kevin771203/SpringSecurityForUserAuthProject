package org.kevinlin.springbootmall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kevinlin.springbootmall.dao.UserDao;
import org.kevinlin.springbootmall.dto.UserLoginRequest;
import org.kevinlin.springbootmall.dto.UserRegisterRequest;
import org.kevinlin.springbootmall.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    private ObjectMapper objectMapper = new ObjectMapper();


    // 註冊新帳號
    @Transactional
    @Test
    void register_success() throws Exception {

        //given
        UserRegisterRequest userRegisterRequest = createUser("test1@gmail.com", "123");


        //when
        RequestBuilder requestBuilder = when_execute(
                getuserRegisterRequestJson(userRegisterRequest),
                "/userRegister"
        );


        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.e_mail", equalTo("test1@gmail.com")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));

        // 檢查資料庫中的密碼不為明碼
        Users user = userDao.getUserByEmail(userRegisterRequest.getEmail());
        assertNotEquals(userRegisterRequest.getPassword(), user.getPassword());
    }

    @Transactional
    @Test
    void register_invalidEmailFormat() throws Exception {

        //given
        UserRegisterRequest userRegisterRequest = createUser("3gd8e7q34l9", "123456");
        String json = getuserRegisterRequestJson(userRegisterRequest);


        //when
        RequestBuilder requestBuilder = when_execute(json, "/userRegister");


        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Transactional
    @Test
    void register_emailAlreadyExist() throws Exception {

        //given
        UserRegisterRequest userRegisterRequest = createUser("test2@gmail.com", "123456");
        String json = getuserRegisterRequestJson(userRegisterRequest);


        //when
        RequestBuilder requestBuilder = when_execute(json, "/userRegister");
        //創建帳號
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));


        //then
        // 再次使用同個 email 註冊
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    void login_success() throws Exception {

        //given
        UserRegisterRequest userRegisterRequest = createUser("test3@gmail.com", "123456");
        register(userRegisterRequest);



        //when
        RequestBuilder requestBuilder = when_execute(
                "/userLogin",
                userRegisterRequest.getEmail(),
                userRegisterRequest.getPassword()
        );


        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200));
    }

    @Test
    void login_wrongPassword() throws Exception {

        //given
        UserRegisterRequest userRegisterRequest = createUser("test4@gmail.com", "123");
        register(userRegisterRequest);
        String wrongPassword = "unknown";


        //when
        RequestBuilder requestBuilder = when_execute(
                "/userLogin",
                userRegisterRequest.getEmail(),
                wrongPassword
        );


        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(401));
    }

    @Test
    void login_invalidEmailFormat() throws Exception {

        //given
        String mail = "hkbudsr324";
        String password = "123";


        //when
        RequestBuilder requestBuilder = when_execute(
                "/userLogin",
                mail,
                password
        );


        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(401));
    }

    @Test
    void login_emailNotExist() throws Exception {

        //given
        String unknownEmail = "unknown@gmail.com";
        String password = "123";


        //when
        RequestBuilder requestBuilder = when_execute(
                "/userLogin",
                unknownEmail,
                password
        );


        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(401));
    }

    private String getuserRegisterRequestJson(UserRegisterRequest userRegisterRequest) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(userRegisterRequest);
        return json;
    }

    private void register(UserRegisterRequest userRegisterRequest) throws Exception {
        String json = getuserRegisterRequestJson(userRegisterRequest);

        RequestBuilder requestBuilder = when_execute(json, "/userRegister");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));
    }

    private static MockHttpServletRequestBuilder when_execute(String path,String email, String password) {
        return MockMvcRequestBuilders
                .get(path)
                .with(httpBasic(
                        email,
                        password
                ));
    }

    private static RequestBuilder when_execute(String json, String path) {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        return requestBuilder;
    }

    private static UserRegisterRequest createUser(String mail, String password) {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail(mail);
        userRegisterRequest.setPassword(password);
        return userRegisterRequest;
    }




}