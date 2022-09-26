package org.javaproteam27.socialnetwork.security;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RequiredArgsConstructor
public class AvailabilityUser {
    private final JdbcTemplate jdbcTemplate;
    private static final String ID = "id";
    private static final String EMAIL = "email";

    public boolean checkUser(String user){
        List<Person> allPerson = findAllPerson();
        for (Person person : allPerson){
            String emailNotSpace = person.getEmail().replaceAll("\\s+","");
            if (emailNotSpace.equals(user)){
                return true;
            }
        }
        return false;
    }

    public List<Person> findAllPerson() {
        String sql = "SELECT * FROM person";
        List<Person> persons = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Person person = new Person();
                    person.setId(rs.getInt(ID));
                    person.setEmail(rs.getString(EMAIL));
                    return person;
                }
        );
        return persons;
    }
}
