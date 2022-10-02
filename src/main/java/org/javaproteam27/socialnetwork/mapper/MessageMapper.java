package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Message;
import org.javaproteam27.socialnetwork.model.enums.ReadStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageMapper implements RowMapper<Message> {
    
    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return Message.builder()
                .id(rs.getInt("id"))
                .time(rs.getTimestamp("time").toLocalDateTime())
                .authorId(rs.getInt("author_id"))
                .recipientId(rs.getInt("recipient_id"))
                .messageText(rs.getString("message_text"))
                .readStatus(ReadStatus.valueOf(rs.getString("read_status")))
                .dialogId(rs.getInt("dialog_id"))
                .isDeleted(rs.getBoolean("is_deleted"))
                .build();
    }
}
