package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.mapper.FriendshipStatusMapper;
import org.javaproteam27.socialnetwork.model.entity.FriendshipStatus;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendshipStatusRepository {

    private final RowMapper<FriendshipStatus> rowMapper = new FriendshipStatusMapper();
    private final JdbcTemplate jdbcTemplate;


    public FriendshipStatus findById(int id) {
        try {
            String sql = "select * from friendship_status where id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("friendship_status id = " + id);
        }
    }


    public int save(FriendshipStatus friendshipStatus) {

        String sql = "insert into friendship_status(time, name, code) " +
                "values (?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setTimestamp(1, Timestamp.valueOf(friendshipStatus.getTime()));
            ps.setString(2, friendshipStatus.getName());
            ps.setString(3, friendshipStatus.getCode().name());
            return ps;
        }, keyHolder);

        Optional<Number> key = Optional.of(keyHolder.getKey());
        return key.get().intValue();
    }

    public List<FriendshipStatus> getApplicationsFriendshipStatus(Integer srcPersonId, Integer id) {
        try {
            String sql = "SELECT * FROM friendship_status fs\n" +
                    "join friendship f on fs.id= f.status_id\n" +
                    "where src_person_id = ? and dst_person_id = ?";
            return jdbcTemplate.query(sql, rowMapper, srcPersonId, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id + " or " + srcPersonId);
        }
    }

    public void updateCode(Integer id, FriendshipStatusCode friendshipStatusCode) {

        String sql = "UPDATE friendship_status SET code = ? where id = ?";
        jdbcTemplate.update(sql, friendshipStatusCode.name(), id);
    }

    public void delete(FriendshipStatus friendshipStatus) {
        String sql = "delete from friendship_status where id = " + friendshipStatus.getId();
        jdbcTemplate.update(sql);
    }

    public List<FriendshipStatus> findByPersonId(Integer dstId, Integer srcId) {
        String sql = "select fs.* from friendship_status fs join friendship f on fs.id = f.status_id " +
                "where f.src_person_id = ? and f.dst_person_id = ?";
        return jdbcTemplate.query(sql, rowMapper, srcId, dstId);
    }

}
