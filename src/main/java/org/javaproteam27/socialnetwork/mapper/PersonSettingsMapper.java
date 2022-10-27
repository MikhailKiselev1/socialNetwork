package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.PersonSettings;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonSettingsMapper implements RowMapper<PersonSettings> {
    @Override
    public PersonSettings mapRow(ResultSet rs, int i) throws SQLException {
        return PersonSettings.builder()
                .id(rs.getInt("id"))
                .personId(rs.getInt("person_id"))
                .postCommentNotification(rs.getBoolean("post_comment_notification"))
                .commentCommentNotification(rs.getBoolean("comment_comment_notification"))
                .friendRequestNotification(rs.getBoolean("friend_request_notification"))
                .messageNotification(rs.getBoolean("message_notification"))
                .friendBirthdayNotification(rs.getBoolean("friend_birthday_notification"))
                .likeNotification(rs.getBoolean("like_notification"))
                .postNotification(rs.getBoolean("post_notification"))
                .build();
    }
}
