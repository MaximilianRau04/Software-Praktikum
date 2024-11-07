package com.sopra.eaplanner.repository;

import com.sopra.eaplanner.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO user (id, username, surname, lastname, is_admin) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM user WHERE id = ?";

    public int save(User user) {
        return jdbcTemplate.update(INSERT_SQL, user.getUsername(), user.getSurname(), user.getLastname(), user.isAdmin());
    }

    public User findByUsername(Long id) {
        return jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new EventRowMapper(), id);
    }

    private static class EventRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("surname"),
                    rs.getString("lastname"),
                    rs.getBoolean("is_admin")
            );
        }
    }
}
