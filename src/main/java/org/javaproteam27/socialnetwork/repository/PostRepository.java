package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.ErrorException;
import org.javaproteam27.socialnetwork.model.entity.Post;
import org.javaproteam27.socialnetwork.mapper.PostMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public Integer addPost(long time, int authorId, String title, String postText) {

        Integer postId;
        try {
            String sqlQuery = "INSERT INTO post (time, author_id, title, post_text) " +
                    "VALUES ('" + new Timestamp(time) + "', " + authorId + ", '" + title + "', '" + postText + "')";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> connection.prepareStatement(sqlQuery, new String[]{"id"}), keyHolder);
            postId = (Integer) keyHolder.getKey();
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return postId;
    }

    public List<Post> findAllUserPosts(int authorId, int offset, int limit) {

        List<Post> retList;
        try {
            retList = jdbcTemplate.query("SELECT * FROM post WHERE author_id = " + authorId
                    + " ORDER BY time DESC" + " LIMIT " + limit + " OFFSET " + offset, new PostMapper());
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return retList;
    }

    public void softDeletePostById(int postId) {

        boolean retValue;
        try {
            retValue = (jdbcTemplate.update("UPDATE post SET is_deleted = true WHERE id = ?", postId) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        if (!retValue) {
            throw new ErrorException("Post not deleted");
        }
    }

    public void finalDeletePostById(int postId){

        boolean retValue;
        try {
            retValue = (jdbcTemplate.update("DELETE FROM post WHERE id = ?", postId) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        if (!retValue) {
            throw new ErrorException("Post not deleted");
        }
    }

    public void updatePostById(int postId, String title, String postText) {

        boolean retValue;
        try {
            retValue = (jdbcTemplate.update("UPDATE post SET title = ?, post_text = ? WHERE id = ?", title,
                    postText, postId) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        if (!retValue) {
            throw new ErrorException("Post not updated");
        }
    }

    public Post findPostById(int postId) {

        Post post;
        try {
            post = jdbcTemplate.queryForObject("SELECT * FROM post WHERE id = ?"
                    , new Object[]{postId}, new PostMapper());
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return post;
    }

    public List<Post> findAllPublishedPosts(int offset, int limit) {

        List<Post> retList;
        try {
            retList = jdbcTemplate.query("SELECT * FROM post WHERE time <= CURRENT_TIMESTAMP " +
                    "AND is_deleted is false ORDER BY time DESC LIMIT " + limit + " OFFSET " + offset, new PostMapper());
            //                "SELECT * FROM post WHERE post_text LIKE '%" + postText + "%'"
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }

        return retList;
    }


    public List<Post> findPost(String text, String dateFrom, String dateTo, String authorName, List<String> tags) {
        ArrayList<String> queryParts = new ArrayList<>();
        StringBuilder query = new StringBuilder();

        if (authorName != null) {
            query.insert(0, " JOIN person AS per ON per.id = p.author_id");
            query.append(" WHERE ");
            queryParts.add("per.first_name = '" + authorName + "'");
        } else {
            query.append(" WHERE ");
        }

        if (dateFrom != null) {
            LocalDateTime dateFromParsed = Instant.ofEpochMilli(Long.parseLong(dateFrom))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            queryParts.add("p.time > '" + dateFromParsed + "'::date");
        }

        if (dateTo != null) {
            LocalDateTime dateToParsed = Instant.ofEpochMilli(Long.parseLong(dateTo))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            queryParts.add("p.time < '" + dateToParsed + "'::date");
        }

        queryParts.add("(p.post_text ILIKE '%" + text + "%' OR p.title ILIKE '%" + text + "%')");

        if (tags != null) {
            queryParts.add(buildQueryTags(tags));
            query.insert(0, " JOIN post2tag AS pt ON p.id = pt.post_id JOIN tag AS t ON t.id = pt.tag_id");
            query.insert(0, "SELECT p.*, count(*) FROM post AS p");
        } else {
            query.insert(0, "SELECT p.* FROM post AS p");
        }

        String buildQuery = query + String.join(" AND ", queryParts) + ";";

        return jdbcTemplate.query(buildQuery, new PostMapper());
    }

    public Integer getCount() {

        String sql = "select count(*) from post";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer getPersonalCount(int id) {

        try {
            String sql = "select count(*) from post where author_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }

    private String buildQueryTags(List<String> tags) {

        List<String> buildQueryTags = new ArrayList<>();
        StringBuilder sb = new StringBuilder("(");

        tags.forEach(tag -> buildQueryTags.add("tag = '" + tag + "'"));
        String buildTags = String.join(" OR ", buildQueryTags);
        sb.append(buildTags).append(")").append(" GROUP BY p.id ORDER BY COUNT(*) DESC");

        return sb.toString();
    }

    public void recoverPostById(int postId) {

        boolean retValue;
        try {
            retValue = (jdbcTemplate.update("UPDATE post SET is_deleted = false WHERE id = ?", postId) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        if (!retValue) {
            throw new ErrorException("Post not recovered");
        }
    }

    public List<Integer> getDeletedPostIdsOlderThan(String interval) {

        try {
            return jdbcTemplate.query("SELECT * FROM post WHERE is_deleted = true AND time < now() - interval '" +
                            interval +"'", (rs, rowNum) -> rs.getInt("id"));
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }
}
