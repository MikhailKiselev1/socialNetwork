package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Currency;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyMapper implements RowMapper<Currency> {
    @Override
    public Currency mapRow(ResultSet rs, int i) throws SQLException {

        return Currency.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .price(rs.getString("price"))
                .build();
    }
}
