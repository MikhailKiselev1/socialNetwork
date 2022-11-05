package org.javaproteam27.socialnetwork.controller;

import org.javaproteam27.socialnetwork.service.KafkaProducerService;
import org.javaproteam27.socialnetwork.util.PhotoCloudinary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
//@TestPropertySource("/application-test.yml")
@ActiveProfiles("test")
@Sql(scripts = {"classpath:sql/person/insert-person.sql", "classpath:sql/person/insert-person-settings.sql"})
@Transactional
@WithUserDetails("test@mail.ru")
public class FriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PhotoCloudinary photoCloudinary;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    private final String friendsUrl = "/api/v1/friends";

    @Test
    public void addFriends() throws Exception {
        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");

        this.mockMvc.perform(post(friendsUrl + "/2"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getListFriends() throws Exception {
        this.mockMvc.perform(get(friendsUrl))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteFriends() throws Exception {

        this.mockMvc.perform(delete(friendsUrl + "/2"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addApplicationsFriends() throws Exception {
        this.mockMvc.perform(post(friendsUrl + "/request/2"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getListApplicationsFriends() throws Exception {
        this.mockMvc.perform(get(friendsUrl + "/request"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteApplicationsFriends() throws Exception {

        this.mockMvc.perform(delete(friendsUrl + "/request/2"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}