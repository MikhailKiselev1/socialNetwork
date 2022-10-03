package org.javaproteam27.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaproteam27.socialnetwork.model.dto.request.CommentRq;
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
        "classpath:sql/post/insert-comment.sql"})
@Transactional
@WithUserDetails("test@mail.ru")
public class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String commentToPostUrl = "/api/v1/post/1/comments";

    @Test
    public void createComment() throws Exception {

        CommentRq rq = CommentRq.builder().commentText("Added in test comment to post 1").build();
        this.mockMvc.perform(post(commentToPostUrl).content(objectMapper.writeValueAsString(rq))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getComments() throws Exception {

        this.mockMvc.perform(get(commentToPostUrl).param("offset", "0").param("perPage", "10"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteComment() throws Exception {

        this.mockMvc.perform(delete(commentToPostUrl + "/1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void editComment() throws Exception {

        CommentRq rq = CommentRq.builder().commentText("New comment from test to post 1").build();
        this.mockMvc.perform(put(commentToPostUrl + "/1").content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}