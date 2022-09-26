package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.FriendshipRs;
import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.javaproteam27.socialnetwork.model.entity.FriendshipStatus;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.javaproteam27.socialnetwork.repository.FriendshipStatusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipStatusService {
    
    private final FriendshipStatusRepository friendshipStatusRepository;
    private final FriendshipService friendshipService;
    
    public FriendshipStatus findById(int id) {
        return friendshipStatusRepository.findById(id);
    }
    public int addStatus(){
        LocalDateTime localDateTime = LocalDateTime.now();

        FriendshipStatus friendshipStatus = new FriendshipStatus();
        friendshipStatus.setTime(localDateTime);
//        friendshipStatus.setName(FriendshipStatusCode.REQUEST.name());
        friendshipStatus.setCode(FriendshipStatusCode.REQUEST);
        return friendshipStatusRepository.save(friendshipStatus);
    }

    public FriendshipRs updateStatus(Integer srcPersonId, Integer id, FriendshipStatusCode friendshipStatusCode){

        List<FriendshipStatus> friendshipStatusList = friendshipStatusRepository.getApplicationsFriendshipStatus(srcPersonId, id);

        for (FriendshipStatus friendshipStatus : friendshipStatusList) {
            friendshipStatusRepository.updateCode(friendshipStatus.getId(),friendshipStatusCode);
        }

        String error = "";
        HashMap<String,String> messageMap = new HashMap<>();
        messageMap.put("message","ok");
        LocalDateTime localDateTime = LocalDateTime.now();

        return new FriendshipRs(
                error,
                localDateTime,
                messageMap);
    }



    public FriendshipRs deleteStatus(List<Friendship> friendshipList){
        for (Friendship friendship : friendshipList){
            FriendshipStatus friendshipStatus = new FriendshipStatus();
            friendshipStatus.setId(friendship.getStatusId());
            friendshipStatusRepository.delete(friendshipStatus);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        String error = "string";
        HashMap<String,String> aa= new HashMap<>();
        aa.put("message","ok");

        FriendshipRs postsResponseDto = new FriendshipRs(
                error,
                localDateTime,
                aa);
        return postsResponseDto;

    }
}
