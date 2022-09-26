package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.PostLike;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostLikeMapper implements RowMapper<PostLike> {

    @Override
    public PostLike mapRow(ResultSet rs, int i) throws SQLException {
        PostLike postLike = new PostLike();

        postLike.setId(rs.getInt("id"));
        postLike.setTime(rs.getTimestamp("time").getTime());
        postLike.setPersonId(rs.getInt("person_id"));
        postLike.setPostId(rs.getInt("post_id"));
        postLike.setType(rs.getString("type"));

        return postLike;
    }
}
