package org.javaproteam27.socialnetwork.service;

import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PostRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.repository.PostRepository;
import org.javaproteam27.socialnetwork.repository.TagRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class PostServiceTest {
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private NotificationService notificationService;

    @Autowired
    PostService postService;

    private void assertResponseRs(ResponseRs<PostRs> response){
        assertNotNull(response);
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    private void assertListResponseRs(ListResponseRs<PostRs> response, Integer offset, Integer itemPerPage){
        assertNotNull(response);
        assertEquals(offset, response.getOffset());
        assertEquals(itemPerPage, response.getPerPage());
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    @Test
    public void publishPost() {
        PostRq testPost = PostRq.builder().postText("Test post").tags(List.of("test_post")).title("Test post").build();
        ResponseRs<PostRs> response = postService.publishPost(System.currentTimeMillis(), testPost, 1);

        assertResponseRs(response);
    }


    @Test
    public void findAllPosts() {
        int offset = 0;
        int itemPerPage = 20;
        ListResponseRs<PostRs> response = postService.findAllPosts(offset, itemPerPage);

        assertListResponseRs(response, offset, itemPerPage);
    }

    @Test
    public void findAllUserPosts() {
        int offset = 0;
        int itemPerPage = 20;
        int authorId = 1;
        ListResponseRs<PostRs> response = postService.findAllUserPosts(authorId, offset, itemPerPage);

        assertListResponseRs(response, offset, itemPerPage);
    }

    @Test
    public void deletePost() {
        int postId = 1;
        ResponseRs<PostRs> response = postService.softDeletePost(postId);

        assertResponseRs(response);
    }

    @Test
    public void updatePost() {
        int postId = 0;
        String title = "post title";
        String postText = "post text";
        List<String> tags = new ArrayList<>();
        ResponseRs<PostRs> response = postService.updatePost(postId, title, postText, tags);

        assertResponseRs(response);
    }

    @Test
    public void findPost() {
        String text = "post text";
        Long dateFrom = System.currentTimeMillis() - 100_000;
        Long dateTo = System.currentTimeMillis();
        String authorName = "author name";
        List<String> tags = new ArrayList<>();
        int offset = 0;
        int itemPerPage = 20;
        ListResponseRs<PostRs> response = postService.findPost(text, dateFrom.toString(), dateTo.toString(),
                authorName, tags, offset, itemPerPage);

        assertListResponseRs(response, offset, itemPerPage);
    }

    @Test
    public void getPost() {
        int postId = 0;
        ResponseRs<PostRs> response = postService.getPost(postId);

        assertResponseRs(response);
    }
}