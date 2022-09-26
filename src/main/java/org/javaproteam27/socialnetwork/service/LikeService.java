package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.LikeRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.entity.PostLike;
import org.javaproteam27.socialnetwork.repository.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PersonService personService;
    private final NotificationsService notificationsService;

    public ResponseRs<LikeRs> addLike(String type, Integer objectLikedId){

        long time = System.currentTimeMillis();
        Integer personId = personService.getAuthorizedPerson().getId();
        Integer likesCount = null;
        List<Integer> userListLiked = null;
        int likeId = likeRepository.addLike(time, personId, objectLikedId, type);
        notificationsService.createPostLikeNotification(likeId, time, objectLikedId, type);
        LikeRs data = LikeRs.builder().likes(1).users(Arrays.asList(personId)).build();
        return new ResponseRs<>("", data, null);
    }

    public ResponseRs<LikeRs> deleteLike(String type, Integer objectLikedId) {

        Integer personId = personService.getAuthorizedPerson().getId();
        likeRepository.deleteLike(type, objectLikedId, personId);
        LikeRs data = LikeRs.builder().likes(1).build();
        return new ResponseRs<>("", data, null);
    }

    public ResponseRs<LikeRs> getLikeList(String type, Integer objectLikedId) {

        List<Integer> likes = likeRepository.getLikedUserList(objectLikedId, type);
        LikeRs data = LikeRs.builder().likes(likes.size()).users(likes).build();
        return new ResponseRs<>("", data, null);
    }

    public Boolean isLikedByUser(Integer userId, Integer objectLikedId, String type){

        List<Integer> likes = likeRepository.isLikedByUser(userId, objectLikedId, type);
        return !likes.isEmpty();
    }

    public Integer countLikes(Integer objectLikedId, String type) {
        return likeRepository.getLikedUserList(objectLikedId, type).size();
    }

    public void deleteAllLikesByLikedObjectId(Integer objectLikedId, String type) {
        likeRepository.deleteLike(type, objectLikedId, null);
    }
}
