package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.service.ConfigurationService;
import com.github.jdubois.responses.service.PropertiesService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * @author Julien Dubois
 */
@Repository
public class ConfigurationServiceImpl implements ConfigurationService {

    public static String staticContent;

    public static String siteUrl;

    public static String googleAnalytics;

    private final Log log = LogFactory.getLog(ConfigurationServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTempate;

    @Autowired
    private PropertiesService propertiesService;

    @PostConstruct
    void init() {
        staticContent = propertiesService.getStaticContent();
        siteUrl = propertiesService.getSiteUrl();
        googleAnalytics = propertiesService.getGoogleAnalytics();
    }

    @Transactional(readOnly = true)
    public int testDatabase() {
        return jdbcTempate.queryForObject("select 1 from dual", Integer.class);
    }
}
