package org.javaproteam27.socialnetwork.util;

import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "logsupload", name = "enabled")
public class LogsUploader {

    private final YandexDisk yandexDisk;

    @Scheduled(initialDelay = 6000, fixedRateString = "PT12H")
    @Async
    private void uploadLogs() {
        try {
            log.info("Started saving logs to remote storage");
            RestClient yaDisk = yandexDisk.getRestClient();
            ProgressListener progressListener = yandexDisk.getProgressListener();
            logsUpload(progressListener, yaDisk);
            archivedLogsUpload(progressListener, yaDisk);
            log.info("Logs successfully uploaded to remote storage");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void archivedLogsUpload(ProgressListener progressListener, RestClient yaDisk) throws ServerException,
            IOException {
        var archivedFiles = new File("logs/archived").listFiles();
        if (archivedFiles != null) {
            for (File file : archivedFiles) {
                var fileName = file.getName();
                String path = "logs/archived/" + fileName;
                Link newLink = yaDisk.getUploadLink(path, true);
                yaDisk.uploadFile(newLink, true, file, progressListener);
            }
        }
    }

    private void logsUpload(ProgressListener progressListener, RestClient yaDisk) throws ServerException,
            IOException {
        Link infoLoggerLink = yaDisk.getUploadLink("logs/info-logger.log", true);
        File infoLoggerFile = new File("logs/info-logger.log");
        yaDisk.uploadFile(infoLoggerLink, true, infoLoggerFile, progressListener);

        Link debugLoggerLink = yaDisk.getUploadLink("logs/debug-logger.log", true);
        File debugLoggerFile = new File("logs/debug-logger.log");
        yaDisk.uploadFile(debugLoggerLink, true, debugLoggerFile, progressListener);
    }

    @Scheduled(initialDelay = 90000, fixedRateString = "PT12H")
    @Async
    private void deleteOldLogs() throws ServerIOException, IOException {
        log.info("Started searching outdated logs");
        var yaDisk = yandexDisk.getRestClient();
        var recourses = yaDisk.getResources(new ResourcesArgs.Builder().setPath("logs/archived").build());
        var items = recourses.getResourceList();
        for (Resource item : items.getItems()) {
            var createDate = item.getCreated();
            if (isItemOutdated(createDate)) {
                log.info(item.getName() + " - is out of date | deleting");
                var path = item.getPath().getPath();
                yaDisk.delete(path, true);
            }
        }
        log.info("Searching outdated logs complete");
    }

    private boolean isItemOutdated(Date date) {
        var currentDate = LocalDateTime.now();
        LocalDateTime createDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return currentDate.isAfter(createDate.plusMonths(1));
    }
}
