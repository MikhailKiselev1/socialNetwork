package org.javaproteam27.socialnetwork.util;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.config.RedisConfig;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.util.DropBox;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class Redis {

    private RedisClient redisClient;
    private RedisConnection<String, String> connection;
    private final PersonRepository personRepository;
    private final DropBox dropBox;
    private final RedisConfig redisConfig;

    private void init() {
            redisClient = new RedisClient(
                    RedisURI.create(redisConfig.getUrl()));
            connection = redisClient.connect();
    }

    public void add(Integer id, String url) {
        connection.set(String.valueOf(id), dropBox.getLinkImages(url));
    }

    public String getUrl(Integer id) {
        return connection.get(String.valueOf(id));
    }

    @Scheduled(initialDelay = 6000, fixedDelayString = "PT24H")
    @Async
    private void updateUrl() {
        if (connection == null) {
            init();
        }
        personRepository.findAll().forEach(person ->
            add(person.getId(), person.getPhoto()));
    }

    public void shutdown() {
        connection.close();
        redisClient.shutdown();
    }

}
