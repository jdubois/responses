package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.SchedulerService;
import com.github.jdubois.responses.service.SearchEngineService;
import com.github.jdubois.responses.service.SearchQueryService;
import com.github.jdubois.responses.service.UserManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Julien Dubois
 */
@Service("schedulerService")
public class SchedulerServiceImpl implements SchedulerService {

    private final Log log = LogFactory.getLog(SchedulerServiceImpl.class);

    @Autowired
    private SearchEngineService searchEngineService;

    @Autowired
    private SearchQueryService searchQueryService;

    @Autowired
    private UserManagementService userManagementService;

    /**
     * Clean up the application. This should be run every weekday night, at 5:00 in the morning.
     */
    public void cleanupApplication() {
        log.debug("Cleaning up the application.");
        //Delete the old SearchQueries
        this.searchQueryService.cleanupSearchQueries();

        //Clean up non-validated users
        this.userManagementService.cleanUpNonValidatedUsers();

        //Rebuild the Lucene index.
        this.searchEngineService.rebuildLuceneIndex();

        //Clean the url caches
        User.profileAsUrlCache.clear();
        Question.titleAsUrlCache.clear();
    }
}