package org.javaproteam27.socialnetwork.service;

import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.StorageDataRs;
import org.javaproteam27.socialnetwork.model.dto.response.StorageRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.util.DropBox;
import org.javaproteam27.socialnetwork.util.PhotoCloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhotoCloudinary photoCloudinary;

    public StorageRs postStorage(MultipartFile image, String token) {
        String imageName = null;

        StorageRs response = new StorageRs();
        response.setError("string");
        response.setTimestamp(System.currentTimeMillis());

        if (image == null) {
            return response;
        }
        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));

        String photo = DropBox.dropBoxUploadImages(image);
        person.setPhoto(photo);
        photoCloudinary.add(person.getId(), photo);
        StorageDataRs storageDataRs = new StorageDataRs();
        storageDataRs.setId(imageName);
        storageDataRs.setOwnerId(person.getId());
        storageDataRs.setFileName(image.getName());
        storageDataRs.setFileFormat(image.getContentType());
        personRepository.savePhoto(person);

        return response;
    }

}
