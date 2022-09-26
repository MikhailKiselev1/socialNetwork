package org.javaproteam27.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.request.LoginRq;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.MessagesPermission;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepositoryTest;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Date;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Sql(value = "classpath:sql/person/delete-person.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String loginUrl = "/api/v1/auth/login";
    private final String logoutUrl = "/api/v1/auth/logout";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void correctLoginTest() throws Exception {
        LoginRq rq = new LoginRq();
        rq.setEmail("test@mail.ru");
        rq.setPassword("test1234");
        this.mockMvc.perform(post(loginUrl).content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void badEmailTest() throws Exception {
        LoginRq rq = new LoginRq();
        rq.setEmail("bad@mail.ru");
        rq.setPassword("test1234");
        this.mockMvc.perform(post(loginUrl).content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getClass().equals(EntityNotFoundException.class));
    }

    @Test
    public void badPasswordTest() throws Exception {
        LoginRq rq = new LoginRq();
        rq.setEmail("test@mail.ru");
        rq.setPassword("bad");
        this.mockMvc.perform(post(loginUrl).content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getClass().equals(InvalidRequestException.class));
    }

    @Test
    public void emptyRequestTest() throws Exception {
        this.mockMvc.perform(post(loginUrl))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void authorizedLogoutTest() throws Exception {
        this.mockMvc.perform(post(logoutUrl))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void unauthorizedLogoutTest() throws Exception {
        this.mockMvc.perform(post(logoutUrl))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }
}