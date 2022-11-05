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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
//@TestPropertySource("/application-test.yml")
@ActiveProfiles("test")
@Sql(scripts = {"classpath:sql/person/insert-person.sql", "classpath:sql/post/insert-post.sql",
        "classpath:sql/currency/insert-currency.sql"})
@Transactional
@WithUserDetails("test@mail.ru")
public class FeedsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PhotoCloudinary photoCloudinary;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    private static final String feedsUrl = "/api/v1/feeds";

    @Test
    public void getAllPost() throws Exception {
        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");
        this.mockMvc.perform(get(feedsUrl).param("offset", "0").param("perPage", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}