package org.javaproteam27.socialnetwork.service;

import org.javaproteam27.socialnetwork.model.dto.response.CommentRs;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class CommentServiceTest {

    @MockBean
    PersonService personService;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    NotificationService notificationsService;

    @Autowired
    CommentService commentService;

    private void assertResponseRs(ResponseRs<CommentRs> response){

        assertNotNull(response);
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    private void assertListResponseRs(ListResponseRs<CommentRs> response, Integer offset, Integer itemPerPage){

        assertNotNull(response);
        assertEquals(offset, response.getOffset());
        assertEquals(itemPerPage, response.getPerPage());
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void addComment() {

        int postId = 0;
        String commentText = "comment text";
        Integer parentId = 0;
        Person person = new Person();
        person.setId(1);
        person.setFirstName("FirstName");
        person.setLastName("LastName");
        person.setPhoto("");
        when(personService.getAuthorizedPerson()).thenReturn(person);
        ResponseRs<CommentRs> response = commentService.addComment(postId, commentText, parentId);

        assertResponseRs(response);
    }

    @Test
    void editComment() {

        int postId = 0;
        int commentId = 0;
        String commentText = "comment text";
        Integer parentId = 0;
        Person person = new Person();
        person.setId(1);
        person.setFirstName("FirstName");
        person.setLastName("LastName");
        person.setPhoto("");
        when(personService.getAuthorizedPerson()).thenReturn(person);
        ResponseRs<CommentRs> response = commentService.editComment(postId, commentId, commentText, parentId);

        assertResponseRs(response);
    }

    @Test
    void getCommentsByPostIdInResponse() {

        int postId = 0;
        int offset = 0;
        int itemPerPage = 20;
        ListResponseRs<CommentRs> response = commentService.getCommentsByPostIdInResponse(postId, offset, itemPerPage);

        assertListResponseRs(response, offset, itemPerPage);
    }

    @Test
    void deleteAllCommentsToPost() {

        int postId = 0;
        commentService.deleteAllCommentsToPost(postId);

        verify(commentRepository, times(1)).getAllCommentsByPostId(postId);
    }

    @Test
    void deleteComment() {

        int postId = 0;
        int commentId = 0;
        ResponseRs<CommentRs> response = commentService.deleteComment(postId, commentId);

        assertResponseRs(response);
    }

    @Test
    void initializeCommentsToPost() {

        Integer postId = 0;
        Integer offset = 0;
        Integer limit = 20;
        List<CommentRs> commentRsList = commentService.getAllUserCommentsToPost(postId, offset, limit);

        assertNotNull(commentRsList);
        verify(commentRepository, times(1)).getAllCommentsByPostIdAndParentId(postId,
                null, offset, limit);
    }
}