package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.ComplexRs;
import org.javaproteam27.socialnetwork.model.dto.response.FriendshipRs;
import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.javaproteam27.socialnetwork.model.entity.FriendshipStatus;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.javaproteam27.socialnetwork.repository.FriendshipStatusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipStatusService {

    private final FriendshipStatusRepository friendshipStatusRepository;

    public FriendshipStatus findById(int id) {
        return friendshipStatusRepository.findById(id);
    }

    public int addRequestStatus() {
        LocalDateTime localDateTime = LocalDateTime.now();

        FriendshipStatus friendshipStatus = new FriendshipStatus();
        friendshipStatus.setTime(localDateTime);
        friendshipStatus.setCode(FriendshipStatusCode.REQUEST);
        return friendshipStatusRepository.save(friendshipStatus);
    }

    public int addReceivedRequestStatus() {
        LocalDateTime localDateTime = LocalDateTime.now();

        FriendshipStatus friendshipStatus = new FriendshipStatus();
        friendshipStatus.setTime(localDateTime);
        friendshipStatus.setCode(FriendshipStatusCode.RECEIVED_REQUEST);
        return friendshipStatusRepository.save(friendshipStatus);
    }

    public FriendshipRs updateStatus(Integer srcPersonId, Integer id, FriendshipStatusCode friendshipStatusCode) {

        List<FriendshipStatus> friendshipStatusList = friendshipStatusRepository.getApplicationsFriendshipStatus(srcPersonId, id);

        for (FriendshipStatus friendshipStatus : friendshipStatusList) {
            friendshipStatusRepository.updateCode(friendshipStatus.getId(), friendshipStatusCode);
        }

        var data = ComplexRs.builder().message("ok").build();
        LocalDateTime localDateTime = LocalDateTime.now();

        return new FriendshipRs("", localDateTime, data);
    }


    public FriendshipRs deleteStatus(List<Friendship> friendshipList) {

        for (Friendship friendship : friendshipList) {
            FriendshipStatus friendshipStatus = new FriendshipStatus();
            friendshipStatus.setId(friendship.getStatusId());
            friendshipStatusRepository.delete(friendshipStatus);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        var data = ComplexRs.builder().message("ok").build();

        return new FriendshipRs("", localDateTime, data);

    }
}
