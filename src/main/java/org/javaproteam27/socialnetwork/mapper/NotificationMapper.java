package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Notification;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements RowMapper<Notification> {

    @Override
    public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Notification.builder()
                .id(rs.getInt("id"))
                .notificationType(NotificationType.valueOf(rs.getString("notification_type")))
                .sentTime(rs.getTimestamp("sent_time").getTime())
                .personId(rs.getInt("person_id"))
                .entityId(rs.getInt("entity_id"))
                .contact(rs.getString("contact"))
                .isRead(rs.getBoolean("is_read"))
                .build();
    }
}
