package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.service.ReportingService;
import com.github.jdubois.responses.service.dto.InstanceStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Repository
public class ReportingServiceImpl implements ReportingService {

    private static final String SQL_INSTANCE_STATISTICS = "select" +
            "`Instance`.`name`             as `instanceName`," +
            "COUNT(`Question`.`id`)        as `questions`," +
            "SUM(`Question`.`answersSize`) as `answers` " +
            "from" +
            "`Question` `Question` " +
            "right outer join `Instance` `Instance` " +
            "on `Question`.`instance_id` = `Instance`.`id` " +
            "group by" +
            "`Instance`.`name` " +
            "order by" +
            "`Instance`.`name`";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<InstanceStatistics> getInstanceStatistics() {
        List<InstanceStatistics> statistics = this.jdbcTemplate.query(
                SQL_INSTANCE_STATISTICS,
                new RowMapper<InstanceStatistics>() {
                    public InstanceStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
                        InstanceStatistics stats = new InstanceStatistics();
                        stats.setInstanceName(rs.getString("InstanceName"));
                        stats.setQuestions(rs.getInt("questions"));
                        stats.setAnswers(rs.getInt("answers"));
                        return stats;
                    }
                });
        return statistics;
    }
}
