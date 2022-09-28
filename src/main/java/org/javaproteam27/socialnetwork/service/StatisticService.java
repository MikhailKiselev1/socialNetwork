package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.DebugLogger;
import org.javaproteam27.socialnetwork.model.dto.response.OverallStatisticRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonalStatisticRs;
import org.javaproteam27.socialnetwork.model.dto.response.StatisticRs;
import org.javaproteam27.socialnetwork.repository.*;
import org.springframework.stereotype.Service;

@Service
@DebugLogger
@RequiredArgsConstructor
public class StatisticService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;
    private final MessageRepository messageRepository;
    private final PersonService personService;


    public StatisticRs getStatistics() {
        return StatisticRs.builder()
                .overallStatisticRs(getOverallStatistics())
                .personalStatisticRs(getPersonalStatistics())
                .build();
    }

    private OverallStatisticRs getOverallStatistics() {
        return OverallStatisticRs.builder()
                .likesCount(likeRepository.getCount())
                .postsCount(postRepository.getCount())
                .commentsCount(commentRepository.getCount())
                .usersCount(personRepository.getCount())
                .build();
    }

    private PersonalStatisticRs getPersonalStatistics() {
        var personId = personService.getAuthorizedPerson().getId();
        return PersonalStatisticRs.builder()
                .likesCount(likeRepository.getPersonalCount(personId))
                .postsCount(postRepository.getPersonalCount(personId))
                .commentsCount(commentRepository.getPersonalCount(personId))
                .messagesCount(messageRepository.getPersonalCount(personId))
                .build();
    }
}
