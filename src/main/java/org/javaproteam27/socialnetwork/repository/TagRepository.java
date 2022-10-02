package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.ErrorException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepository {
    private final JdbcTemplate jdbcTemplate;
    public void addTag(String tagString, int postId) {
        try {
            Integer tagId;
            List<Integer> idTags = jdbcTemplate.query("SELECT id FROM tag WHERE tag = '" + tagString + "'",
                    (rs, rowNum) -> rs.getInt("id"));
            if (idTags.isEmpty()) {
                String sqlInsertQuery = "INSERT INTO tag (tag) VALUES ('" + tagString + "')";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> connection.prepareStatement(sqlInsertQuery, new String[]{"id"}), keyHolder);
                tagId = (Integer) keyHolder.getKey();
            } else {
                Optional<Integer> id = Optional.of(idTags.get(0));
                tagId = id.get();
            }
            jdbcTemplate.update("INSERT INTO post2tag (tag_id, post_id) " + "VALUES (?, ?)", tagId, postId);
        } catch (DataAccessException exception){
            throw new ErrorException(exception.getMessage());
        }
    }

    private List<Integer> getTagIdsByPostId(int postId) throws DataAccessException {
        return jdbcTemplate.query("SELECT tag_id FROM post2tag WHERE post_id = " + postId,
                (rs, rowNum) -> rs.getInt("tag_id"));
    }

    public List<String> findTagsByPostId(int postId) {
        List<String> retList;
        try {
            List<Integer> tagIds = getTagIdsByPostId(postId);
            ArrayList<String> tags = new ArrayList<>();
            tagIds.forEach(tagId -> tags.add(jdbcTemplate.queryForObject("SELECT tag FROM tag WHERE id = " + tagId,
                    String.class)));
            retList = tags;
        } catch (DataAccessException exception){
            throw new ErrorException(exception.getMessage());
        }
        return retList;
    }

    public Boolean deleteTagsByPostId(int postId) {
        boolean retValue;
        try {
            List<Integer> tagIds = getTagIdsByPostId(postId);
            tagIds.forEach(tagId -> {
                jdbcTemplate.update("DELETE FROM post2tag WHERE tag_id = ?", tagId);
                jdbcTemplate.update("DELETE FROM tag WHERE id = ?", tagId);
            });
            retValue = true;
        } catch (DataAccessException exception){
            throw new ErrorException(exception.getMessage());
        }
        return retValue;
    }

    public Boolean updateTagsPostId(int postId, List<String> tags) {
        Boolean retValue = null;
        if (Boolean.TRUE.equals(deleteTagsByPostId(postId))) {
            tags.forEach(tag -> addTag(tag, postId));
            retValue = true;
        }
        return retValue;
    }
}
