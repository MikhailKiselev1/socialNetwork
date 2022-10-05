package org.javaproteam27.socialnetwork.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.javaproteam27.socialnetwork.model.enums.MessagesPermission;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class Person {

    private int id;
    private String firstName;
    private String lastName;
    private LocalDateTime regDate;
    private LocalDateTime birthDate;
    private String email;
    private String phone;
    private String password;
    private String photo;
    private String about;
    private String city;
    private String country;
    private Integer confirmationCode;
    private Boolean isApproved;
    private MessagesPermission messagesPermission;
    private LocalDateTime lastOnlineTime;
    private Boolean isBlocked;
    private String token;
    private String changePasswordToken;

    public void setBirthDate(LocalDateTime parse, int i, ZoneOffset utc) {
    }
}
