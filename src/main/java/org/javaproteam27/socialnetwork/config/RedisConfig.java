package org.javaproteam27.socialnetwork.config;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.util.DropBox;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RedisConfig {

    private RedisClient redisClient;
    private RedisConnection<String, String> connection;
    private final PersonRepository personRepository;
    private final DropBox dropBox;

    private void init() {
            redisClient = new RedisClient(
                    RedisURI.create("redis://127.0.0.1:6379"));
            connection = redisClient.connect();
    }

    private void add(Integer id, String url) {
        connection.set(String.valueOf(id), url);
    }

    public String getUrl(Integer id) {
        return connection.get(String.valueOf(id));
    }

    @Scheduled(fixedDelayString = "PT24H")
    @Async
    private void updateUrl() {
        if (connection == null) {
            init();
        }
        personRepository.findAll().forEach(person ->
            add(person.getId(), dropBox.getLinkImages(person.getPhoto())));
    }

    public void shutdown() {
        connection.close();
        redisClient.shutdown();
    }

}
