package org.javaproteam27.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaproteam27.socialnetwork.util.Redis;
import org.javaproteam27.socialnetwork.model.dto.request.UserRq;
import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.security.jwt.JwtUser;
import org.javaproteam27.socialnetwork.service.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"classpath:sql/person/insert-person.sql",
                "classpath:sql/post/insert-post.sql",
                "classpath:sql/city/insert-city.sql",
                "classpath:sql/currency/insert-currency.sql"})
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private Redis redis;

    private final static String meUrl = "/api/v1/users/me";
    private final static String userUrl = "/api/v1/users";


    private String getTokenAuthorization() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        when(redis.getUrl(anyInt())).thenReturn("test");
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


    @Test
    @WithUserDetails("test@mail.ru")
    public void publishPost() throws Exception {

        PostRq rq = PostRq.builder().postText("New post from test").tags(List.of()).title("Test post").build();
        long dayInMillis = 86_400_000L;
        long pubDate = System.currentTimeMillis() + dayInMillis;
        this.mockMvc.perform(post(userUrl + "/1/wall").param("publish_date", Long.toString(pubDate))
                .content(objectMapper.writeValueAsString(rq))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void getUserPosts() throws Exception {

        this.mockMvc.perform(get(userUrl + "/1/wall").param("offset", "0").param("perPage", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void getUserInfo() throws Exception {

        this.mockMvc.perform(get(userUrl + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
