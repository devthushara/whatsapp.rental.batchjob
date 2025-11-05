package com.zoomigo.whatsapp.batchmailsender.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Generic reader using JdbcTemplate; returns List<Map<String,Object>>
 */
@Component
public class GenericJdbcReader {
    private static final Logger log = LoggerFactory.getLogger(GenericJdbcReader.class);
    private final JdbcTemplate jdbc;

    public GenericJdbcReader(JdbcTemplate jdbc){ this.jdbc = jdbc; }

    public List<Map<String, Object>> read(String sql, int limit, int offset) {
        String paged = sql + " LIMIT " + limit + " OFFSET " + offset;
        log.debug("Executing SQL: {}", paged);
        List<Map<String, Object>> rows = jdbc.queryForList(paged);
        log.info("SQL returned {} rows", rows.size());
        return rows;
    }
}
