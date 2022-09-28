package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.ErrorException;
import org.javaproteam27.socialnetwork.mapper.PostLikeMapper;
import org.javaproteam27.socialnetwork.model.entity.PostLike;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private final RowMapper<PostLike> rowMapper = new PostLikeMapper();
    private final JdbcTemplate jdbcTemplate;

    public Integer addLike(long time, Integer personId, Integer objectLikedId, String type) {
        try {
            String sql = "INSERT INTO post_like (time, person_id, post_id, type) VALUES ('" +
                    new Timestamp(time) + "', " + personId + ", " + objectLikedId + ", '" + type + "')";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> connection.prepareStatement(sql, new String[]{"id"}), keyHolder);
            return (Integer) keyHolder.getKey();
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public void deleteLike(String type, Integer objectLikedId, Integer personId) {
        try {
            if (personId != null) {
                jdbcTemplate.update("DELETE FROM post_like WHERE post_id = ? AND type = ? AND person_id = ?",
                        objectLikedId, type, personId);
            } else {
                jdbcTemplate.update("DELETE FROM post_like WHERE post_id = ? AND type = ?",
                        objectLikedId, type);
            }
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public List<Integer> getLikedUserList(Integer objectLikedId, String type) {
        List<Integer> retList;
        try {
            retList = jdbcTemplate.query("SELECT person_id FROM post_like WHERE post_id = " + objectLikedId
                    + " AND type = '" + type + "'", (rs, rowNum) -> rs.getInt("person_id"));
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return retList;
    }

    public List<Integer> isLikedByUser(Integer userId, Integer objectLikedId, String type) {
        List<Integer> retList;
        try {
            retList = jdbcTemplate.query("SELECT id FROM post_like WHERE person_id = " + userId +
                    " AND post_id = " + objectLikedId + " AND type = '" + type + "'", (rs, rowNum) -> rs.getInt("id"));
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return retList;
    }

    public PostLike findById(Integer id) {
        try {
            String sql = "select * from post_like where id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }

    public Integer getCount() {
        String sql = "select count(*) from post_like";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer getPersonalCount(int id) {
        try {
            String sql = "select count(*) from post_like where person_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }
}
