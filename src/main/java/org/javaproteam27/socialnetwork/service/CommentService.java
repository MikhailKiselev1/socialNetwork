package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.InfoLogger;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.CommentRs;
import org.javaproteam27.socialnetwork.model.entity.Comment;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PersonService personService;
    private final CommentRepository commentRepository;
    private final LikeService likeService;
    private final NotificationsService notificationsService;
    public final String COMMENT_MARKER = "Comment";

    private CommentRs convertToCommentRs(Comment comment) {
        Person person = personService.findById(comment.getAuthorId());
        PersonRs author = PersonRs.builder().id(person.getId()).firstName(person.getFirstName()).
                lastName(person.getLastName()).photo(person.getPhoto()).build();
        return CommentRs.builder().isDeleted(false).parentId(comment.getParentId()).
                commentText(comment.getCommentText()).id(comment.getId()).postId(comment.getPostId()).
                time(comment.getTime()).author(author).isBlocked(person.getIsBlocked()).subComments(new ArrayList<>())
                .subLikes(likeService.countLikes(comment.getId(), COMMENT_MARKER))
                .myLike(likeService.isLikedByUser(personService.getAuthorizedPerson().getId(), comment.getId(), COMMENT_MARKER)).
                build();
    }

    public ResponseRs<CommentRs> addComment(int postId, String commentText, Integer parentId) {
        Person person = personService.getAuthorizedPerson();
        PersonRs author = PersonRs.builder().id(person.getId()).firstName(person.getFirstName()).
                lastName(person.getLastName()).photo(person.getPhoto()).build();
        Long time = System.currentTimeMillis();
        Integer commentId = commentRepository.addComment(postId, commentText, parentId, author.getId(), time);
        notificationsService.createCommentNotification(postId, time, commentId, parentId);
        if (parentId != null) {
            notificationsService.createSubCommentNotification(parentId, time, commentId);
        }
        CommentRs data = CommentRs.builder().isDeleted(false).parentId(parentId).commentText(commentText).id(commentId).
                postId(postId).time(time).author(author).isBlocked(false).subComments(new ArrayList<>()).build();
        return new ResponseRs<>("", data, null);

    }

    public ResponseRs<CommentRs> editComment(int postId, int commentId, String commentText, Integer parentId){
        Person person = personService.getAuthorizedPerson();
        PersonRs author = PersonRs.builder().id(person.getId()).firstName(person.getFirstName()).
                lastName(person.getLastName()).photo(person.getPhoto()).build();
        Long time = System.currentTimeMillis();
        commentRepository.editComment(postId, commentId, commentText, time);
        CommentRs data = CommentRs.builder().isDeleted(false).parentId(parentId).commentText(commentText).id(commentId).
                postId(postId).time(time).author(author).isBlocked(false).subComments(new ArrayList<>()).build();
        return new ResponseRs<>("", data, null);
    }

    public ListResponseRs<CommentRs> getCommentsByPostIdInResponse(int postId, int offset, int itemPerPage) {
        return new ListResponseRs<>("", offset, itemPerPage, InitializeCommentsToPost(postId, offset, itemPerPage));
    }

    public void deleteAllCommentsToPost(int postId) {
        List<Comment> comments = commentRepository.getAllCommentsByPostId(postId);
        if (comments.isEmpty())
            return;
        comments.forEach(comment -> deleteComment(postId, comment.getId()));
    }

    public ResponseRs<CommentRs> deleteComment(int postId, int commentId) {

        commentRepository.deleteComment(postId, commentId);
        return new ResponseRs<>("", CommentRs.builder().id(commentId).build(), null);
    }

    public List<CommentRs> InitializeCommentsToPost(Integer postId, Integer offset, Integer limit){
        List<Comment> commentList = commentRepository.getAllCommentsByPostIdAndParentId(postId, null, offset, limit);
        List<CommentRs> commentRsList = commentList.stream().map(this::convertToCommentRs).collect(Collectors.toList());
        commentRsList.forEach(commentRs -> setSubCommentsToComments(commentRs, commentRs.getId()));
        return commentRsList;
    }

    private void setSubCommentsToComments (CommentRs commentRs, Integer parentId) {
        List<Comment> comments = commentRepository.getAllCommentsByPostIdAndParentId(commentRs.getPostId(),
                parentId, null, null);
        List<CommentRs> subComments = comments.stream().map(this::convertToCommentRs).collect(Collectors.toList());
        commentRs.setSubComments(subComments);
        commentRs.getSubComments().forEach(commentRs1 -> setSubCommentsToComments(commentRs1, commentRs1.getId()));
    }
}
