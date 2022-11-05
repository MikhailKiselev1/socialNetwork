package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserRq {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("birth_date")
    private String birthDate;
    private String phone;

    @JsonProperty("photo_id")
    private String photoId;
    private String about;
    private String city;
    private String country;

    @JsonProperty("messages_permission")
    private String messagesPermission;
}
