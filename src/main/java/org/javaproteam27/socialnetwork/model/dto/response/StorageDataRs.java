package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.Data;

@Data
public class StorageDataRs {

    private String id;
    private int ownerId;
    private String fileName;
    private String relativeFilePath;
    private String fileFormat;
    private int bytes;
    private String fileType;
    private int createdAt;
}
