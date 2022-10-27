package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.mapper.CurrencyMapper;
import org.javaproteam27.socialnetwork.model.entity.Currency;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CurrencyRepository {

    private final RowMapper<Currency> rowMapper = new CurrencyMapper();
    private final JdbcTemplate jdbcTemplate;

    public void saveOrUpdate(Currency currency) {
        String sql = "insert into currency (name, price) values (?, ?) on conflict (name) do update set price = ?";
        jdbcTemplate.update(sql, currency.getName(), currency.getPrice(), currency.getPrice());
    }

    public Currency findByName(String name) {
        try {
            String sql = "select * from currency where name = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("name = " + name);
        }
    }
}
