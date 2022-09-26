package org.javaproteam27.socialnetwork.util;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.RestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.javaproteam27.socialnetwork.config.YandexDiskConfig;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class YandexDisk {

    private final YandexDiskConfig yandexDiskConfig;

    public RestClient getRestClient() {
        Credentials credentials = new Credentials(yandexDiskConfig.getLogin(), yandexDiskConfig.getToken());
        return new RestClient(credentials);
    }

    public ProgressListener getProgressListener() {
        return new ProgressListener() {
            @Override
            public void updateProgress(long l, long l1) {

            }

            @Override
            public boolean hasCancelled() {
                return false;
            }
        };
    }
}
