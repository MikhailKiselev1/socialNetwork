package org.javaproteam27.socialnetwork.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.config.RedisConfig;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class Redis {

    // Объект для работы с Redis
    private RedissonClient redisson;
    //хэшмеп для фоток
    private RMap<String, String> usersPhoto;
    private final String name = "USER_PHOTO";
    private final PersonRepository personRepository;
    private final DropBox dropBox;
    private final RedisConfig redisConfig;

    private void init() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisConfig.getUrl());
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            System.out.println("Не удалось подключиться к Redis");
            System.out.println(Exc.getMessage());
        }
        usersPhoto = redisson.getMap(name);
    }

    public void add(Integer id, String url) {
        String currentUrl = dropBox.getLinkImages(url);
        if (usersPhoto.containsKey(String.valueOf(id))) {
            usersPhoto.fastRemove(String.valueOf(id));
        }
        usersPhoto.fastPut(String.valueOf(id), currentUrl);
    }

    public String getUrl(Integer id) {
        return usersPhoto.get(String.valueOf(id));
    }

    @Scheduled(initialDelay = 6000, fixedDelayString = "PT24H")
    @Async
    private void updateUrl() {
        if (redisson == null) {
            init();
        }
        personRepository.findAll().forEach(person ->
            add(person.getId(), person.getPhoto()));
    }

    public void shutdown() {
        redisson.shutdown();
    }

}
