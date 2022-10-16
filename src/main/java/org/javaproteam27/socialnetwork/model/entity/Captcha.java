package org.javaproteam27.socialnetwork.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Captcha {

    private int id;
    private LocalDateTime time;
    private String code;
    private String secretCode;
}
