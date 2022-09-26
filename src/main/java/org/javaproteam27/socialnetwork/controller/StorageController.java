package org.javaproteam27.socialnetwork.controller;

import com.dropbox.core.DbxException;
import com.yandex.disk.rest.exceptions.ServerException;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.StorageRs;
import org.javaproteam27.socialnetwork.service.StorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StorageRs postStorage( @RequestHeader("Authorization") String token,
                                  @RequestParam MultipartFile file) throws ServerException, IOException, DbxException {
        storageService.dropBoxUploadImages(file);
        return storageService.postStorage(file, token);
    }
}
