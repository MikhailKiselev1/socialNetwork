package org.javaproteam27.socialnetwork.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Transactional
public class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String statisticUrl = "/api/v1/statistics";

    @Test
    @WithUserDetails("test@mail.ru")
    public void getStatisticsAuthorizedPersonIsOkResponseWithJsonContent() throws Exception {
        this.mockMvc.perform(get(statisticUrl))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getStatisticsUnAuthorizedPersonAccessDeniedResponse() throws Exception {
        this.mockMvc.perform(get(statisticUrl))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }
}