package org.javaproteam27.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaproteam27.socialnetwork.model.dto.request.LikeRq;
import org.javaproteam27.socialnetwork.service.KafkaProducerService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
//@TestPropertySource("/application-test.yml")
@ActiveProfiles("test")
@Sql(scripts = {"classpath:sql/person/insert-person.sql",
        "classpath:sql/post/insert-post.sql",
        "classpath:sql/post/insert-like.sql",
        "classpath:sql/person/insert-person-settings.sql"})
@Transactional
@WithUserDetails("test@mail.ru")
public class LikesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String likesToPostUrl = "/api/v1/likes";

    @Test
    public void putLike() throws Exception {

        LikeRq rq = LikeRq.builder().type("Post").itemId(1).build();
        this.mockMvc.perform(put(likesToPostUrl).content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteLike() throws Exception {

        this.mockMvc.perform(delete(likesToPostUrl).param("item_id", "1").param("type", "Post"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getLikeList() throws Exception {

        this.mockMvc.perform(get(likesToPostUrl).param("item_id", "1").param("type", "Post"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}