package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipMapper implements RowMapper<Friendship> {
    
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
    
        Friendship friendship = new Friendship();
    
        friendship.setId(rs.getInt("id"));
        friendship.setStatusId(rs.getInt("status_id"));
        friendship.setSentTime(rs.getTimestamp("sent_time").toLocalDateTime());
        friendship.setSrcPersonId(rs.getInt("src_person_id"));
        friendship.setDstPersonId(rs.getInt("dst_person_id"));
    
        return friendship;
    }
}
