package org.javaproteam27.socialnetwork.controller;

import com.dropbox.core.DbxException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.StorageRs;
import org.javaproteam27.socialnetwork.service.StorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "storage", description = "Взаимодействие с хранилищем")
public class StorageController {

    private final StorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StorageRs postStorage(@RequestHeader("Authorization") String token,
                                 @RequestParam MultipartFile file) throws DbxException {
        return storageService.postStorage(file, token);
    }
}
