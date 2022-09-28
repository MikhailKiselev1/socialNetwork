package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.model.dto.response.CommentRs;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PostRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.entity.Post;
import org.javaproteam27.socialnetwork.repository.PostRepository;
import org.javaproteam27.socialnetwork.repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final NotificationService notificationService;
    private final String POST_MARKER = "Post";

    private final PersonService personService;

    private PostRs convertToPostRs(Post post){

        if (post == null) return null;
        long timestamp = post.getTime();
        String type = (timestamp > System.currentTimeMillis()) ? "QUEUED" : "POSTED";
        List<CommentRs> comments = commentService.InitializeCommentsToPost(post.getId(), null, null);
        return PostRs.builder()
                .id(post.getId())
                .time(timestamp)//.toLocalDateTime())
                .author(personService.initialize(post.getAuthorId()))
                .title(post.getTitle())
                .likes(likeService.countLikes(post.getId(), POST_MARKER))
                .tags(tagRepository.findTagsByPostId(post.getId()))
                .commentRs(comments)
                .type(type)
                .postText(post.getPostText())
                .isBlocked(post.getIsBlocked()).myLike(false)
                .myLike(likeService.isLikedByUser(personService.getAuthorizedPerson().getId(), post.getId(), POST_MARKER))
                .build();
    }

    public ListResponseRs<PostRs> findAllPosts(int offset, int itemPerPage) {
        List<Post> posts = postRepository.findAllPublishedPosts(offset, itemPerPage);
        List<PostRs> data = (posts != null) ? posts.stream().map(this::convertToPostRs).
                collect(Collectors.toList()) : null;
        return new ListResponseRs<>("", offset, itemPerPage, data);
    }

    public ListResponseRs<PostRs> findAllUserPosts(int authorId, int offset, int itemPerPage) {
        List<Post> posts = postRepository.findAllUserPosts(authorId, offset, itemPerPage);
        List<PostRs> data = (posts != null) ? posts.stream().map(this::convertToPostRs).
                collect(Collectors.toList()) : null;
        return new ListResponseRs<>("", offset, itemPerPage, data);
    }

    @Transactional
    public ResponseRs<PostRs> deletePost(int postId) {

        tagRepository.deleteTagsByPostId(postId);
        List<Integer> commentIds = commentService.InitializeCommentsToPost(postId, null, null).stream().
                map(CommentRs::getId).collect(Collectors.toList());
        commentIds.forEach(commentId -> likeService.deleteAllLikesByLikedObjectId(commentId,
                commentService.COMMENT_MARKER));
        likeService.deleteAllLikesByLikedObjectId(postId, POST_MARKER);
        commentService.deleteAllCommentsToPost(postId);
        postRepository.deletePostById(postId);
        return new ResponseRs<>("", PostRs.builder().id(postId).build(),null);
    }

    public ResponseRs<PostRs> updatePost(int postId, String title, String postText, ArrayList<String> tags) {
        tagRepository.updateTagsPostId(postId, tags);
        postRepository.updatePostById(postId, title, postText);
        return new ResponseRs<>("", convertToPostRs(postRepository.findPostById(postId)),null);
    }

    public ResponseRs<PostRs> publishPost(Long publishDate, PostRq postRq, int authorId) {
        long publishDateTime = (publishDate == null) ? System.currentTimeMillis() : publishDate;
        int postId = postRepository.addPost(publishDateTime, authorId, postRq.getTitle(), postRq.getPostText());
        postRq.getTags().forEach(tag -> tagRepository.addTag(tag, postId));
        notificationService.createPostNotification(authorId, publishDateTime, postId);
        return (new ResponseRs<>("", convertToPostRs(postRepository.findPostById(postId)),null));
    }

    public ResponseEntity<?> findPost (String text, Long dateFrom, Long dateTo, String authorName, List<String> tags,
                                       int offset, int itemPerPage) {

        List<Post> postsFound = postRepository.findPost(text, dateFrom, dateTo, authorName, tags);
        List<PostRs> data = (postsFound != null) ? postsFound.stream().map(this::convertToPostRs).
                collect(Collectors.toList()) : null;

        return ResponseEntity.ok(new ListResponseRs<>("", offset, itemPerPage, data));
    }

    public ResponseRs<PostRs> getPost(int postId) {
        Post post = postRepository.findPostById(postId);
        PostRs data = (post != null) ? convertToPostRs(post) : null;
        return new ResponseRs<>("", data, null);
    }
}
