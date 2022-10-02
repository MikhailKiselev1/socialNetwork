package org.javaproteam27.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.request.LoginRq;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonRepository personRepository;

    private final String loginUrl = "/api/v1/auth/login";
    private final String logoutUrl = "/api/v1/auth/logout";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        var password = passwordEncoder.encode("test1234");
        var person = personRepository.findById(1);
        person.setPassword(password);
        personRepository.editPassword(person);
    }

    @Test
    public void loginCorrectRqIsOkResponseWithJsonContent() throws Exception {
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
    public void loginBadEmailRqEntityNotFoundThrown() throws Exception {
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
    public void loginBadPasswordRqInvalidRequestThrown() throws Exception {
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
    public void loginEmptyRequest400Response() throws Exception {
        this.mockMvc.perform(post(loginUrl))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void logoutAuthorizedRqIsOkResponse() throws Exception {
        this.mockMvc.perform(post(logoutUrl))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void logoutUnAuthorizedRqAccessDeniedResponse() throws Exception {
        this.mockMvc.perform(post(logoutUrl))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }
}