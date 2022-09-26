package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.DebugLogger;
import org.javaproteam27.socialnetwork.model.dto.response.EntityAuthorRs;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.NotificationBaseRs;
import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.javaproteam27.socialnetwork.model.entity.Notification;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;
import org.javaproteam27.socialnetwork.repository.*;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@DebugLogger
public class NotificationsService {

    private final PersonService personService;
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final NotificationRepository notificationRepository;
    private final FriendshipRepository friendshipRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;


    public ListResponseRs<NotificationBaseRs> getNotifications(String token, int offset, int itemPerPage) {
        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        var notificationList = notificationRepository.findByPersonId(person.getId());
        var currentTime = System.currentTimeMillis();

        List<NotificationBaseRs> result = new ArrayList<>();
        for (Notification notification : notificationList) {
            if (!notification.isRead() && notification.getSentTime() < currentTime) {
                result.add(getNotificationRs(notification));
            }
        }
        return new ListResponseRs<>("", offset, itemPerPage, result);
    }

    public ListResponseRs<NotificationBaseRs> markAsReadNotification(String token, int id, boolean all) {
        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        var notificationList = notificationRepository.findByPersonId(person.getId());
        int itemPerPage = all ? 20 : 1;
        List<NotificationBaseRs> result = new ArrayList<>();
        if (all) {
            for (Notification notification : notificationList) {
                if (!notification.isRead()) {
                    notification.setRead(true);
                    notificationRepository.updateReadStatus(notification);
                    result.add(getNotificationRs(notification));
                }
            }
        } else {
            Notification notification = notificationRepository.findById(id);
            notification.setRead(true);
            notificationRepository.updateReadStatus(notification);
            result.add(getNotificationRs(notification));
        }
        return new ListResponseRs<>("", 0, itemPerPage, result);
    }

    public void createCommentNotification(int postId, Long sentTime, int commentId, Integer parentId) {
        var postAuthor = postRepository.findPostById(postId).getAuthorId();
        var personId = personService.getAuthorizedPerson().getId();
        if (parentId != null) {
            var commentAuthor = commentRepository.getCommentById(parentId).getAuthorId();
            if (!Objects.equals(commentAuthor, postAuthor) && postAuthor != personId) {
                createNotification(postAuthor, NotificationType.POST_COMMENT, commentId, sentTime);
            }
        } else if (postAuthor != personId) {
            createNotification(postAuthor, NotificationType.POST_COMMENT, commentId, sentTime);
        }
    }

    public void createSubCommentNotification(int parentId, Long sentTime, int commentId) {
        var commentAuthor = commentRepository.getCommentById(parentId).getAuthorId();
        var personId = personService.getAuthorizedPerson().getId();
        if (commentAuthor != personId) {
            createNotification(commentAuthor, NotificationType.COMMENT_COMMENT, commentId, sentTime);
        }
    }

    public void createFriendshipNotification(int dstId, int friendshipStatusId, int srcId) {
        var friendship = friendshipRepository.findOneByIdAndFriendshipStatus(srcId,
                dstId, friendshipStatusId);
        var sentTime = System.currentTimeMillis();
        createNotification(dstId, NotificationType.FRIEND_REQUEST, friendship.getId(), sentTime);
    }

    public void createPostNotification(int authorId, Long publishDate, int postId) {
        var friendList = friendshipRepository.findAllFriendsByPersonId(authorId);
        for (Friendship friendship : friendList) {
            var friendId = friendship.getDstPersonId();
            createNotification(friendId, NotificationType.POST, postId, publishDate);
        }
    }

    public void createPostLikeNotification(int likeId, Long time, int postId, String type) {
        Integer personId = personService.getAuthorizedPerson().getId();
        if (Objects.equals(type, "Post")) {
            Integer authorId = postRepository.findPostById(postId).getAuthorId();
            if (!authorId.equals(personId)) {
                createNotification(authorId, NotificationType.POST_LIKE, likeId, time);
            }
        }
        if (Objects.equals(type, "Comment")) {
            Integer authorId = commentRepository.getCommentById(postId).getAuthorId();
            if (!authorId.equals(personId)) {
                createNotification(authorId, NotificationType.COMMENT_LIKE, likeId, time);
            }
        }
    }

    private void createNotification(int dstId, NotificationType notificationType, int entityId, Long sentTime) {
        Notification notification = new Notification();
        notification.setSentTime(sentTime);
        notification.setPersonId(dstId);
        notification.setEntityId(entityId);
        notification.setNotificationType(notificationType);
        notification.setContact("");
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    private NotificationBaseRs getNotificationRs(Notification notification) {
        return NotificationBaseRs.builder()
                .id(notification.getId())
                .info(getInfoFromType(notification))
                .sentTime(notification.getSentTime())
                .notificationType(notification.getNotificationType())
                .entityId(notification.getEntityId())
                .entityAuthor(getEntityAuthor(notification))
                .build();
    }

    private EntityAuthorRs getEntityAuthor(Notification notification) {
        int entityId = notification.getEntityId();
        Integer authorId = null;
        switch (notification.getNotificationType()) {
            case POST:
                authorId = postRepository.findPostById(entityId).getAuthorId();
                break;
            case POST_COMMENT:
            case COMMENT_COMMENT:
                authorId = commentRepository.getCommentById(entityId).getAuthorId();
                break;
            case FRIEND_REQUEST:
                authorId = friendshipRepository.findById(entityId).getSrcPersonId();
                break;
            case POST_LIKE:
            case COMMENT_LIKE:
                authorId = likeRepository.findById(entityId).getPersonId();
                break;
        }
        if (authorId != null) {
            var person = personRepository.findById(authorId);
            return EntityAuthorRs.builder().firstName(person.getFirstName()).lastName(person.getLastName())
                    .photo(person.getPhoto()).build();
        }
        return null;
    }

    private String getInfoFromType(Notification notification) {
        int entityId = notification.getEntityId();
        switch (notification.getNotificationType()) {
            case POST:
                return postRepository.findPostById(entityId).getTitle();
            case POST_COMMENT:
            case COMMENT_COMMENT:
                return commentRepository.getCommentById(entityId).getCommentText();
            case FRIEND_REQUEST:
                var personId = friendshipRepository.findById(entityId).getSrcPersonId();
                return getFullNameFromPerson(personRepository.findById(personId));
            case POST_LIKE:
            case COMMENT_LIKE:
                var postLikePersonId = likeRepository.findById(entityId).getPersonId();
                return getFullNameFromPerson(personRepository.findById(postLikePersonId));
        }
        return null;
    }

    private String getFullNameFromPerson(Person person) {
        return person.getFirstName() + " " + person.getLastName();
    }
}
