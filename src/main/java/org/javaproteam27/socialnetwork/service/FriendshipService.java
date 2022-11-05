package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.response.ComplexRs;
import org.javaproteam27.socialnetwork.model.dto.response.FriendshipRs;
import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;
import org.javaproteam27.socialnetwork.repository.FriendshipRepository;
import org.javaproteam27.socialnetwork.repository.FriendshipStatusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;
    private final FriendshipStatusRepository friendshipStatusRepository;
    private final PersonService personService;
    private final FriendshipStatusService friendshipStatusService;


    public void save(Friendship friendship) {
        friendshipRepository.save(friendship);
    }

    public List<Friendship> findByPersonId(int id) {
        return friendshipRepository.findByPersonId(id);
    }

    public List<Friendship> findByPersonIdAndStatus(Integer id, FriendshipStatusCode statusCode) {
        return friendshipRepository.findByPersonIdAndStatus(id, statusCode);
    }

    public List<Friendship> requestVerification(int id, int srcPersonId) {
        return friendshipRepository.getStatus(id, srcPersonId);
    }

    public FriendshipRs addFriendShip(int id) {
        Person person = personService.getAuthorizedPerson();

        int friendshipStatusId;
        if (requestVerification(id, person.getId()).isEmpty() && person.getId() != id) {
            friendshipStatusId = friendshipStatusService.addRequestStatus();
            var friendshipStatusReceivedId = friendshipStatusService.addReceivedRequestStatus();
            createFriendShip(person.getId(), friendshipStatusReceivedId, id);
            return createFriendShip(id, friendshipStatusId, person.getId());
        } else {
            friendshipStatusId = -1;
        }
        return createFriendShip(id, friendshipStatusId, person.getId());
    }

    private FriendshipRs createFriendShip(int id, int friendshipStatusId, int srcPersonId) {
        LocalDateTime localDateTime = LocalDateTime.now();

        if (friendshipStatusId != -1) {
            Friendship friendship = new Friendship();
            friendship.setStatusId(friendshipStatusId);
            friendship.setSentTime(localDateTime);
            friendship.setSrcPersonId(srcPersonId);
            friendship.setDstPersonId(id);
            friendshipRepository.save(friendship);

            var status = friendshipStatusRepository.findById(friendshipStatusId);
            if (status.getCode() == FriendshipStatusCode.REQUEST) {
                notificationService.createFriendshipNotification(id, friendshipStatusId, srcPersonId);
            }

            var data = ComplexRs.builder().message("ok").build();
            return new FriendshipRs("", localDateTime, data);
        } else {
            throw new InvalidRequestException("Friend request already exists");
        }
    }

    public FriendshipRs deleteFriend(int id) {
        Person person = personService.getAuthorizedPerson();
        int srcPersonId = person.getId();

        List<Friendship> friendshipList = findByFriendShip(srcPersonId, id);
        deleteFriendShip(srcPersonId, id);
        deleteFriendShip(id, srcPersonId);

        return friendshipStatusService.deleteStatus(friendshipList);
    }


    public void deleteFriendShip(int srcPersonId, int dstPersonId) {
        Friendship friendship = new Friendship();
        friendship.setSrcPersonId(srcPersonId);
        friendship.setDstPersonId(dstPersonId);
        var friendshipList = friendshipRepository.findByFriendShip(srcPersonId, dstPersonId);
        friendshipRepository.delete(friendship);
        if (!friendshipList.isEmpty()) {
            var entityId = friendshipList.get(0).getId();
            notificationService.deleteNotification(NotificationType.FRIEND_REQUEST, dstPersonId, entityId);
        }
    }

    public List<Friendship> findByFriendShip(int srcPersonId, int dstPersonId) {
        return friendshipRepository.findByFriendShip(srcPersonId, dstPersonId);
    }

    public FriendshipRs addApplicationsFriends(int id) {
        Person person = personService.getAuthorizedPerson();
        friendshipStatusService.updateStatus(person.getId(), id, FriendshipStatusCode.FRIEND);
        return friendshipStatusService.updateStatus(id, person.getId(), FriendshipStatusCode.FRIEND);
    }

    public FriendshipRs deleteApplicationsFriends(int id) {
        Person person = personService.getAuthorizedPerson();
        friendshipStatusService.updateStatus(person.getId(), id, FriendshipStatusCode.SUBSCRIBED);
        return friendshipStatusService.updateStatus(id, person.getId(), FriendshipStatusCode.DECLINED);
    }


}
