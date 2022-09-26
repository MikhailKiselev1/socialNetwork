package org.javaproteam27.socialnetwork.model.dto.request;

import lombok.Data;

@Data
public class LoginRq {
    private String email;
    private String password;
}
