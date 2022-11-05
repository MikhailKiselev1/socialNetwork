package org.javaproteam27.socialnetwork.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.repository.PersonRepository;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoCloudinary {

    //хэшмеп для фоток
    private ConcurrentHashMap<String, String> usersPhoto;
    private final PersonRepository personRepository;
    private final DropBox dropBox;


    private void init() {
        usersPhoto = new ConcurrentHashMap<>();
    }

    public void add(Integer id, String url) {
        String currentUrl = dropBox.getLinkImages(url);
        if (!usersPhoto.containsKey(String.valueOf(id))) {
            usersPhoto.put(String.valueOf(id), currentUrl);
        } else {
            usersPhoto.replace(String.valueOf(id), currentUrl);
        }
    }

    public String getUrl(Integer id) {
        return usersPhoto.get(String.valueOf(id));
    }

    @Scheduled(initialDelay = 6000, fixedDelayString = "PT2H")
    @Async
    public void updateUrl() {
        if (usersPhoto == null) {
            init();
        }

        personRepository.findAll().forEach(person ->
                add(person.getId(), person.getPhoto()));

    }



}
