package org.javaproteam27.socialnetwork.controller;

import com.dropbox.core.DbxException;
import org.javaproteam27.socialnetwork.model.dto.request.LoginRq;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

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
@Sql({"classpath:sql/notification/insert-notification.sql"})
@Transactional
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;

    private final String notificationUrl = "/api/v1/notifications";


    @Test
    public void getNotificationsAuthorizedPersonIsOkResponseWithJsonContent() throws Exception {
        this.mockMvc.perform(get(notificationUrl).header("Authorization", getTokenAuthorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void markAsReadNotificationsAuthorizedPersonWithAllTrueIsOkResponseWithJsonContent() throws Exception {
        this.mockMvc.perform(put(notificationUrl).header("Authorization", getTokenAuthorization())
                        .param("all", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private String getTokenAuthorization() throws IOException, DbxException {
        LoginRq rq = new LoginRq();
        rq.setEmail("test@mail.ru");
        rq.setPassword("test1234");
        ResponseRs<PersonRs> loginRs = loginService.login(rq);
        return loginRs.getData().getToken();
    }
}
