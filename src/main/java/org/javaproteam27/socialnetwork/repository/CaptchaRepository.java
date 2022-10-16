package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.ErrorException;
import org.javaproteam27.socialnetwork.mapper.CaptchaMapper;
import org.javaproteam27.socialnetwork.mapper.PostMapper;
import org.javaproteam27.socialnetwork.model.entity.Captcha;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.entity.Post;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CaptchaRepository {

    private final JdbcTemplate jdbcTemplate;


    public int addCaptcha(long time, String code, String secretCode) {

        try {
            if(jdbcTemplate.queryForObject("SELECT MAX(id) FROM captcha", Integer.class) == null) {
                jdbcTemplate.update("INSERT INTO captcha (id, time, code, secret_code) " +
                        "VALUES (?, ?, ?, ?)", 1, new Timestamp(time), code, secretCode);
                return 1;
            }
            int id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM captcha", Integer.class) + 1;
            jdbcTemplate.update("INSERT INTO captcha (id, time, code, secret_code) " +
                    "VALUES (?, ?, ?, ?)", id, new Timestamp(time), code, secretCode);
            return id;
        } catch (DataAccessException exception){
            return -1;
        }
    }


    public Captcha findByCode(String code) {
        Captcha captcha;
        captcha = jdbcTemplate.queryForObject("SELECT * FROM captcha WHERE code = ?", new Object[]{code},
                new CaptchaMapper());
        return captcha;
    }

    public List<Captcha> findAll() {
        List<Captcha> retList;
        try {
            retList = jdbcTemplate.query("SELECT * FROM captcha", new CaptchaMapper());
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return retList;

    }

    public boolean deleteCaptcha(Captcha captcha) {

        boolean retValue;
        try {
            retValue = (jdbcTemplate.update("DELETE FROM captcha WHERE id = ?", captcha.getId()) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return retValue;
    }

    public void save(Captcha captcha) {

        String sql = "insert into captcha(time, code, secret_code) values (?,?,?)";
        jdbcTemplate.update(sql, captcha.getTime(), captcha.getCode(), captcha.getSecretCode());
    }
}
