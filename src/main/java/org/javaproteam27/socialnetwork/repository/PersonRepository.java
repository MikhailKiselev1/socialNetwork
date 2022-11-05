package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.ErrorException;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.mapper.PersonMapper;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonRepository {

    public static final String PERSON_ID = "person id = ";
    private final RowMapper<Person> rowMapper = new PersonMapper();
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipRepository friendshipRepository;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final PersonSettingsRepository personSettingsRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;



    public Integer save(Person person) {
        String sql = "insert into person(first_name, last_name, reg_date, email, " +
                "password, photo, is_approved, last_online_time, is_deleted)" +
                "values ('%s','%s','%s','%s','%s','%s','%b','%s','%s')";
        String sqlFormat = String.format(sql,
                person.getFirstName(),
                person.getLastName(),
                new Timestamp(person.getRegDate()),
                person.getEmail(),
                person.getPassword(),
                person.getPhoto(),
                person.getIsApproved(),
                new Timestamp(person.getLastOnlineTime()),
                person.getIsDeleted());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> connection.prepareStatement(sqlFormat, new String[]{"id"}), keyHolder);
        return (Integer) keyHolder.getKey();
    }

    public Person findById(int id) {
        try {
            String sql = "select * from person where id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public Person findNotDeletedById(int id) {
        try {
            String sql = "select * from person where is_deleted = false and id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public List<Person> getFriendsByPersonId(Integer id) {
        try {
            String sql = "SELECT * FROM friendship f\n" +
                    "join friendship_status fs on fs.id=f.status_id\n" +
                    "join person p on f.dst_person_id=p.id\n" +
                    "where fs.code = 'FRIEND' and src_person_id = ?";
            return jdbcTemplate.query(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }


    public Person findByEmail(String email) {
        try {
            String sql = "select * from person where email like ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException("Пользователь с почтой - " + email + " не найден.");
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

        return filterBlockedPeople(jdbcTemplate.query(buildQuery, rowMapper), authorizedPerson.getId());
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

    public List<Person> getFriendsPersonById(Integer id) {
        try {
            String sql;
            sql = "SELECT * FROM person p \n" +
                    "join friendship f on f.dst_person_id = p.id\n" +
                    "join friendship_status fs on fs.id = f.status_id\n" +
                    "where fs.code = 'FRIEND' and is_deleted is false " +
                    "and src_person_id = ? or dst_person_id = ?";


            return jdbcTemplate.query(sql, rowMapper, id, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public List<Person> getApplicationsFriendsPersonById(Integer id) {
        try {
            String sql;
            sql = "SELECT * FROM person p \n" +
                    "join friendship f on f.src_person_id = p.id\n" +
                    "join friendship_status fs on fs.id = f.status_id\n" +
                    "where fs.code = 'REQUEST' and dst_person_id = ? and is_deleted is false";

            return jdbcTemplate.query(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }

    public Boolean editPerson(Person person) {
        boolean retValue;
        try {
            retValue = (jdbcTemplate.update("UPDATE person SET first_name = ?, last_name = ?, " +
                            "birth_date = ?, phone = ?, about = ?, city = ?, country = ?, messages_permission = ? " +
                            "WHERE id = ?", person.getFirstName(), person.getLastName(),
                    new Timestamp(person.getBirthDate()),
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

    public void savePhoto(Person person) {
        try {
            jdbcTemplate.update("UPDATE person SET photo = ? WHERE id = ?", person.getPhoto(),
                    person.getId());
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public void updateEmail(Person person) {
        try {
            jdbcTemplate.update("UPDATE person SET email = ? WHERE id = ?", person.getEmail(),
                    person.getId());
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public void editPasswordToken(Person person) {
        try {
            jdbcTemplate.update("UPDATE person SET change_password_token = ? WHERE id = ?",
                    person.getChangePasswordToken(), person.getId());
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

    public Boolean fullDeletePerson(Person person) {
        try {
            dialogRepository.findByPersonId(person.getId()).forEach(dialog -> {
                messageRepository.deleteByDialogId(dialog.getId());
                dialogRepository.deleteById(dialog.getId());
            });

            friendshipRepository.findByPersonId(person.getId()).forEach(friendshipRepository::delete);
            personSettingsRepository.delete(person.getId());
            postRepository.findAllUserPosts(person.getId()).forEach(post -> {
                tagRepository.deleteTagsByPostId(post.getId());
                postRepository.finalDeletePostById(post.getId());
            });

            return (jdbcTemplate.update("DELETE FROM person WHERE id = ?", person.getId()) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }

    public void updateNotificationsSessionId(Person person) {
        String sql = "update person set notifications_session_id = ?, online_status = ?, last_online_time = ? " +
                "where id = ?";
        jdbcTemplate.update(sql, person.getNotificationsSessionId(),
                person.getOnlineStatus(), new Timestamp(person.getLastOnlineTime()), person.getId());
    }

    public List<Person> findBySessionId(String sessionId) {
        String sql = "select * from person where notifications_session_id = ?";
        return jdbcTemplate.query(sql, rowMapper, sessionId);
    }

    public void deleteSessionId(Person person) {
        String sql = "update person set notifications_session_id = null, online_status = 'OFFLINE', " +
                "last_online_time = ? where id = ?";
        jdbcTemplate.update(sql, new Timestamp(person.getLastOnlineTime()), person.getId());
    }

    public boolean checkEmailExists(String email) {
        String sql = "select * from person where email = ?";
        var rs = jdbcTemplate.query(sql, rowMapper, email);
        return !rs.isEmpty();
    }

    public boolean setPersonIsDeleted(Person person) {
        try {
            return (jdbcTemplate.update("UPDATE person SET is_deleted = ?, deleted_time = ? WHERE id = ?",
                    person.getIsDeleted(), LocalDateTime.now(), person.getId()) == 1);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }
}