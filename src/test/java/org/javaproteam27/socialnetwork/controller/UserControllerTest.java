package org.javaproteam27.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaproteam27.socialnetwork.model.dto.request.UserRq;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.security.jwt.JwtUser;
import org.javaproteam27.socialnetwork.service.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Transactional
public class UserControllerTest {

    private final static String meUrl = "/api/v1/users/me";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ObjectMapper objectMapper;


    private String getTokenAuthorization() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        return jwtTokenProvider.createToken(jwtUser.getUsername());
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void profileResponseAuthorizedPersonIsOkResponseWithJsonContent() throws Exception {
        this.mockMvc.perform(get(meUrl).header("Authorization", getTokenAuthorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void profileResponseUnAuthorizedPersonAccessDeniedResponse() throws Exception {
        this.mockMvc.perform(get(meUrl))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void profileEditInformation() throws Exception {
        UserRq userRq = new UserRq();
        userRq.setFirstName("Тест");
        userRq.setLastName("Тестов");
        userRq.setBirthDate("1987-07-01T00:00:00+04:00");
        userRq.setCountry("Россия");
        userRq.setCity("Москва");
        userRq.setPhone("8064581946");

        this.mockMvc.perform(put(meUrl).header("Authorization", getTokenAuthorization())
                .content(objectMapper.writeValueAsString(userRq))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
