package com.sopra.eaplanner.repository;

import com.sopra.eaplanner.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EventRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO event (name, start_time, end_time, location, trainer, description, exchange_day_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_EXCHANGE_DAY_ID_SQL = "SELECT * FROM event WHERE exchange_day_id = ?";

    public int save(Event event) {
        return jdbcTemplate.update(INSERT_SQL,
                event.getName(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getTrainer(),
                event.getDescription()
        );
    }

    public List<Event> findByExchangeDayId(Long exchangeDayId) {
        return jdbcTemplate.query(SELECT_BY_EXCHANGE_DAY_ID_SQL, new EventRowMapper(), exchangeDayId);
    }

    private static class EventRowMapper implements RowMapper<Event> {

        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Event(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getString("location"),
                    rs.getString("trainer"),
                    rs.getLong("exchange_day_id")
            );
        }
    }
}
