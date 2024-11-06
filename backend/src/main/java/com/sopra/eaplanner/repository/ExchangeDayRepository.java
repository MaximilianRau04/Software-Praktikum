package com.sopra.eaplanner.repository;

import com.sopra.eaplanner.model.ExchangeDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ExchangeDayRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO exchange_day (date, location, description) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM exchange_day";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM exchange_day WHERE id = ?";

    public int save(ExchangeDay exchangeDay) {
        return jdbcTemplate.update(INSERT_SQL, exchangeDay.getDate(), exchangeDay.getLocation(), exchangeDay.getDescription());
    }

    public List<ExchangeDay> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, new ExchangeDayRowMapper());
    }

    public ExchangeDay findById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new ExchangeDayRowMapper(), id);
    }

    private static class ExchangeDayRowMapper implements RowMapper<ExchangeDay> {
        @Override
        public ExchangeDay mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ExchangeDay(
                    rs.getLong("id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("location"),
                    rs.getString("description")
            );
        }
    }
}
