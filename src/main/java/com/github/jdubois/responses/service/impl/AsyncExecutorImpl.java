package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.service.AsyncExecutor;
import com.github.jdubois.responses.service.QuestionService;
import com.github.jdubois.responses.service.SchedulerService;
import com.github.jdubois.responses.service.SearchQueryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Julien Dubois
 */
@Service
public class AsyncExecutorImpl implements AsyncExecutor, ApplicationContextAware {

    private final Log log = LogFactory.getLog(AsyncExecutorImpl.class);

    private ApplicationContext applicationContext;

    @Autowired
    private SearchQueryService searchQueryService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private QuestionService questionService;

    @Async
    public void asyncCleanupApplication() {
        log.info("Asynchronous call to clean up the application");
        schedulerService.cleanupApplication();
    }

    @Async
    public void asyncAddSearchQuery(int instanceId, String query) {
        if (log.isDebugEnabled()) {
            log.debug("Asynchronous call to add a query to the SearchQuery entities");
        }
        try {
            this.searchQueryService.addSearchQuery(instanceId, query);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    @Async
    public void countQuestionView(Long questionId) {
        questionService.countQuestionView(questionId);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
