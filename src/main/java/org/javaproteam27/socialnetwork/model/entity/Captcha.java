package org.javaproteam27.socialnetwork.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Captcha {

    private int id;
    private LocalDateTime time;
    private String code;
    private String secretCode;
}
