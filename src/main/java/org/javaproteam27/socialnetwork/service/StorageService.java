package org.javaproteam27.socialnetwork.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.yandex.disk.rest.exceptions.ServerException;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.StorageDataRs;
import org.javaproteam27.socialnetwork.model.dto.response.StorageRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.util.DropBox;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DropBox dropBox;
    private String imageName;

    public StorageRs postStorage(MultipartFile image, String token) throws ServerException, IOException, DbxException {

        StorageRs response = new StorageRs();
        response.setError("string");
        response.setTimestamp(System.currentTimeMillis());

        if (image == null) {
            return response;
        }
        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));

        person.setPhoto(dropBoxUploadImages(image));
        StorageDataRs storageDataRs = new StorageDataRs();
        storageDataRs.setId(imageName);
        storageDataRs.setOwnerId(person.getId());
        storageDataRs.setFileName(image.getName());
        storageDataRs.setFileFormat(image.getContentType());
        personRepository.savePhoto(person);

        return response;
    }


    public String dropBoxUploadImages(MultipartFile image) throws DbxException, IOException {
        imageName = "/" + UUID.randomUUID().toString() + "." + image.getOriginalFilename().split("\\.")[1];

        String token = dropBox.getRefreshToken();
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/javaproteams_27").build();
        DbxClientV2 client = new DbxClientV2(config, token);
        try (InputStream in = image.getInputStream()) {
            FileMetadata metadata = client.files().uploadBuilder(imageName)
                    .uploadAndFinish(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client.files().getTemporaryLink(imageName).getLink();
    }
}
