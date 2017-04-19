package ru.holyway.botplatform.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 4/19/2017.
 */
@Component
public class DataHelper {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, List<String>> getLearn() {
        return jdbcTemplate.queryForObject("SELECT data FROM lear_dictionary where chat_id = 1", Map.class);
    }

    public List<String> getSimple() {
        return jdbcTemplate.queryForObject("SELECT data FROM lear_dictionary where chat_id = 1", List.class);
    }

    public Settings getSettings() {
        return jdbcTemplate.queryForObject("SELECT data FROM lear_dictionary where chat_id = 1", Settings.class);
    }
}
