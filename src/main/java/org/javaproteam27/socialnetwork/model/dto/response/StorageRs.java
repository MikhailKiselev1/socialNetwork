package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.Data;

@Data
public class StorageRs {

    private String error;
    private long timestamp;
    private StorageDataRs data;
}
