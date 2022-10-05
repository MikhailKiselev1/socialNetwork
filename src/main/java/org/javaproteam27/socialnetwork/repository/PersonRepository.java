package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.ErrorException;
import org.javaproteam27.socialnetwork.mapper.PersonMapper;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PersonRepository {

    private final RowMapper<Person> rowMapper = new PersonMapper();
    private final JdbcTemplate jdbcTemplate;


    public void save(Person person) {

        String sql = "insert into person(first_name, last_name, reg_date, birth_date, email, phone, " +
                "password, photo, about, city, country, confirmation_code, is_approved, messages_permission, " +
                "last_online_time, is_blocked) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, person.getFirstName(), person.getLastName(), person.getRegDate(),
                person.getBirthDate(), person.getEmail(), person.getPhone(), person.getPassword(),
                person.getPhoto(), person.getAbout(), person.getCity(), person.getCountry(), person.getConfirmationCode(),
                person.getIsApproved(), person.getMessagesPermission(), person.getLastOnlineTime(),
                person.getIsBlocked());
    }

    public Person findById(int id) {
        try {
            String sql = "select * from person where id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id);
        }
    }

    public List<Person> getFriendsPersonById(Integer id) {
        try {
            String sql = "SELECT * FROM friendship f\n" +
                    "join friendship_status fs on fs.id=f.status_id\n" +
                    "join person p on f.dst_person_id=p.id\n" +
                    "where fs.code = 'FRIEND' and src_person_id = ?";
            return jdbcTemplate.query(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id);
        }
    }


    public Person findByEmail(String email) {
        try {
            String sql = "select * from person where email like ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person email = " + email);
        }
    }

    public Integer getCount() {
        
        String sql = "select count(*) from person";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Person getPersonById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM person WHERE id = " + id, Person.class);
    }

    public List<Person> findPeople(Person authorizedPerson, String firstName, String lastName, Integer ageFrom,
                                   Integer ageTo, String city, String country) {

        ArrayList<String> queryParts = new ArrayList<>();

        if (firstName != null) {
            queryParts.add("first_name = '" + firstName + "'");
        }

        if (lastName != null) {
            queryParts.add("last_name = '" + lastName + "'");
        }

        if (ageFrom != null) {
            queryParts.add("date_part('year', age(birth_date))::int > " + ageFrom);
        }

        if (ageTo != null) {
            queryParts.add("date_part('year', age(birth_date))::int < " + ageTo);
        }

        if (city != null) {
            queryParts.add("city = '" + city + "'");
        }

        if (country != null) {
            queryParts.add("country = '" + country + "'");
        }

        String buildQuery = "SELECT * FROM person WHERE id != " + authorizedPerson.getId() + " AND "
                + String.join(" AND ", queryParts) + ";";

        List<Person> filtered = filterBlockedPeople(jdbcTemplate.query(buildQuery, rowMapper), authorizedPerson.getId());

        return filtered;
    }

    private List<Person> filterBlockedPeople(List<Person> peopleFound, int authorizedPersonId) {

        List<Person> blockedPeople = new ArrayList<>();

        String query = "SELECT * FROM person as p JOIN friendship AS f ON p.id = f.dst_person_id\n" +
                "JOIN friendship_status AS fs ON f.status_id = fs.id WHERE f.src_person_id = " + authorizedPersonId +
                " AND f.dst_person_id = ? AND fs.code = 'BLOCKED';";

        peopleFound.stream().map(person -> jdbcTemplate.query(query, rowMapper, person.getId()))
                .forEach(blockedPeople::addAll);

        peopleFound.removeAll(blockedPeople);

        return peopleFound;
    }

    public List<Person> getFriendsPersonById(String name, Integer id) {
        try {
            String sql;
            if (name != null && name.length() != 0) {
                sql = "SELECT * FROM person p \n" +
                        "join friendship f on f.dst_person_id = p.id\n" +
                        "join friendship_status fs on fs.id = f.status_id\n" +
                        "where fs.code = 'FRIEND' and src_person_id = ?" +
                        "and first_name like '%" + name + "%'";
            } else {
                sql = "SELECT * FROM person p \n" +
                        "join friendship f on f.dst_person_id = p.id\n" +
                        "join friendship_status fs on fs.id = f.status_id\n" +
                        "where fs.code = 'FRIEND' and src_person_id = ?";
            }

            return jdbcTemplate.query(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id);
        }
    }

    public List<Person> getApplicationsFriendsPersonById(String name, Integer id) {
        try {
            String sql;
            if (name != null && name.length() != 0) {
                sql = "SELECT * FROM person p \n" +
                        "join friendship f on f.dst_person_id = p.id\n" +
                        "join friendship_status fs on fs.id = f.status_id\n" +
                        "where fs.code = 'REQUEST' and src_person_id = ?" +
                        "and first_name like '%" + name + "%'";
            } else {
                sql = "SELECT * FROM person p \n" +
                        "join friendship f on f.dst_person_id = p.id\n" +
                        "join friendship_status fs on fs.id = f.status_id\n" +
                        "where fs.code = 'REQUEST' and src_person_id = ?";
            }
            return jdbcTemplate.query(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id);
        }
    }

    public Boolean editPerson(Person person) {
        Boolean retValue;
        try {
            retValue = (jdbcTemplate.update("UPDATE person SET first_name = ?, last_name = ?, " +
                    "birth_date = ?, phone = ?, about = ?, city = ?, country = ?, messages_permission = ? " +
                    "WHERE id = ?", person.getFirstName(), person.getLastName(), person.getBirthDate(),
                    person.getPhone(), person.getAbout(), person.getCity(), person.getCountry(),
                    person.getMessagesPermission().toString(), person.getId()) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return retValue;
    }

    public Boolean editPassword(Person person) {
        try {
            return (jdbcTemplate.update("UPDATE person SET password = ? WHERE id = ?", person.getPassword(),
                    person.getId()) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public Boolean savePhoto(Person person) {
        try {
            return (jdbcTemplate.update("UPDATE person SET photo = ? WHERE id = ?", person.getPhoto(),
                    person.getId()) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public Boolean editPasswordToken(Person person) {
        try {
            return (jdbcTemplate.update("UPDATE person SET change_password_token = ? WHERE id = ?",
                    person.getChangePasswordToken(), person.getId()) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public List<Person> getByBirthDay(String birthDay) {
        try {
            String sql = "SELECT * FROM person where birth_date = '" + birthDay + "'";
            return jdbcTemplate.query(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("birth_date = " + birthDay);
        }
    }

    public List<Person> findAll() {
        return jdbcTemplate.query("select * from person", rowMapper);
    }

    public Boolean deletePerson(int id) {
        try {
            return (jdbcTemplate.update("DELETE * FROM person WHERE id = ?", id) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }
}