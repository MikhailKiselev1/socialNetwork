package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.MessagesPermission;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {
    
    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
    
        Person person = new Person();
    
        person.setId(rs.getInt("id"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        person.setRegDate(rs.getTimestamp("reg_date").toLocalDateTime());
//        person.setBirthDate(rs.getTimestamp("birth_date").toLocalDateTime());
        person.setEmail(rs.getString("email"));
        person.setPhone(rs.getString("phone"));
        person.setPassword(rs.getString("password"));
        person.setPhoto(rs.getString("photo"));
        person.setAbout(rs.getString("about"));
        person.setCity(rs.getString("city"));
        person.setCountry(rs.getString("country"));
        person.setConfirmationCode(rs.getInt("confirmation_code"));
        person.setIsApproved(rs.getBoolean("is_approved"));
//        person.setMessagesPermission(MessagesPermission.valueOf(rs.getString("messages_permission")));
//        person.setLastOnlineTime(rs.getTimestamp("last_online_time").toLocalDateTime());
        person.setIsBlocked(rs.getBoolean("is_blocked"));
    
        return person;
    }
}
