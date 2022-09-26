package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.mapper.FriendshipMapper;
import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {

    private final RowMapper<Friendship> rowMapper = new FriendshipMapper();
    private final JdbcTemplate jdbcTemplate;


    public void save(Friendship friendship) {

        String sql = "insert into friendship(status_id, sent_time, src_person_id, dst_person_id) " +
                "values (?,?,?,?)";
        jdbcTemplate.update(sql, friendship.getStatusId(), friendship.getSentTime(),
                friendship.getSrcPersonId(), friendship.getDstPersonId());
    }

    public List<Friendship> findByPersonId(int id) {
        try {
            String sql = "select * from friendship where src_person_id = ? or dst_person_id = ?";
            return jdbcTemplate.query(sql, rowMapper, id, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id);
        }
    }

    public List<Friendship> findByPersonIdAndStatus(Integer id, FriendshipStatusCode statusCode) {
        try {
            String sql = "select * from friendship fs " +
                    "join friendship_status fss on fs.status_id = fss.id " +
                    "where (src_person_id = ? or dst_person_id = ?) and code like ?";
            return jdbcTemplate.query(sql, rowMapper, id, id, statusCode.toString());
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id + " and status = " + statusCode);
        }
    }


    public void delete(Friendship friendship) {
        try {
            String sql = "delete from friendship where src_person_id = " + friendship.getSrcPersonId() + " AND  dst_person_id = " + friendship.getDstPersonId();
            jdbcTemplate.update(sql);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + friendship.getSrcPersonId() + " or " + friendship.getDstPersonId());
        }

    }

    public List<Friendship> findByFriendShip(int srcPersonId, int dstPersonId) {
        try {
            String sql = "select * from friendship where src_person_id = ? and dst_person_id = ?";
            return jdbcTemplate.query(sql, rowMapper, srcPersonId, dstPersonId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + srcPersonId + " or " + dstPersonId);
        }
    }

    public Friendship findOneByIdAndFriendshipStatus(int srcPersonId, int dstPersonId, int statusId) {
        try {
            String sql = "select * from friendship where src_person_id = ? and dst_person_id = ? and status_id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, srcPersonId, dstPersonId, statusId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + srcPersonId + " or " + dstPersonId + " or " + statusId);
        }
    }

    public List<Friendship> findAllFriendsByPersonId(Integer id) {
        try {
            String sql = "select * from friendship fs " +
                    "join friendship_status fss on fs.status_id = fss.id " +
                    "where (src_person_id = ?) and code like 'FRIEND'";
            return jdbcTemplate.query(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id);
        }
    }

    public Friendship findById(Integer id) {
        try {
            String sql = "select * from friendship where id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }
}
