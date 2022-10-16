package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.FriendshipRs;
import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.javaproteam27.socialnetwork.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;


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

    public FriendshipRs addFriendShip(int id, int friendshipStatusId, int srcPersonId){
        HashMap<String, String> messageMap = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        if (friendshipStatusId != -1) {
            Friendship friendship = new Friendship();
            friendship.setStatusId(friendshipStatusId);
            friendship.setSentTime(localDateTime);
            friendship.setSrcPersonId(srcPersonId);
            friendship.setDstPersonId(id);

            friendshipRepository.save(friendship);
            notificationService.createFriendshipNotification(id, friendshipStatusId, srcPersonId);


            messageMap.put("message", "ok");

            return new FriendshipRs(
                    "",
                    localDateTime,
                    messageMap);
        }else {
            messageMap.put("message", "Запрос уже отправлен");
            return new FriendshipRs(
                    "",
                    localDateTime,
                    messageMap);
        }
    }




    public void deleteFriendShip(int srcPersonId, int dstPersonId){
        Friendship friendship = new Friendship();
        friendship.setSrcPersonId(srcPersonId);
        friendship.setDstPersonId(dstPersonId);
        friendshipRepository.delete(friendship);
    }

    public List<Friendship> findByFriendShip(int srcPersonId, int dstPersonId){
        return friendshipRepository.findByFriendShip(srcPersonId, dstPersonId);
    }


}
