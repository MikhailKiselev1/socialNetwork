package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.PostLike;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostLikeMapper implements RowMapper<PostLike> {

    @Override
    public PostLike mapRow(ResultSet rs, int i) throws SQLException {
        return PostLike.builder()
                .id(rs.getInt("id"))
                .time(rs.getTimestamp("time").getTime())
                .personId(rs.getInt("person_id"))
                .postId(rs.getInt("post_id"))
                .type(rs.getString("type"))
                .build();
    }
}
