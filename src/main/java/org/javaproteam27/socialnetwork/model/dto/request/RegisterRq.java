package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRq {

    private String email;

    @JsonProperty("passwd1")
    private String password1;

    @JsonProperty("passwd2")
    private String password2;
    private String firstName;
    private String lastName;
    private String code;
    private String codeSecret;

}
