package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.SearchQuery;
import com.github.jdubois.responses.service.SearchQueryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Repository
public class SearchQueryServiceImpl implements SearchQueryService {

    private final Log log = LogFactory.getLog(SearchQueryServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void addSearchQuery(int instanceId, String query) {
        Query q = em.createNamedQuery("SearchQuery.getSearchQuery");
        q.setParameter("text", query);
        q.setParameter("id", instanceId);
        List results = q.getResultList();
        SearchQuery searchQuery = null;
        if (results.isEmpty()) {
            searchQuery = new SearchQuery();
            searchQuery.setText(query);
            searchQuery.setSize(1);
            Instance instance = em.find(Instance.class, instanceId);
            searchQuery.setInstance(instance);
            em.persist(searchQuery);
        } else {
            searchQuery = (SearchQuery) results.get(0);
            searchQuery.setSize(searchQuery.getSize() + 1);
        }
        searchQuery.setUpdateDate(Calendar.getInstance().getTime());
    }

    @Transactional
    public void cleanupSearchQueries() {
        log.info("Cleaning up old SearchQueries");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        //Queries less than one week old
        Query query = em.createQuery("select s from SearchQuery s where s.size < 5 and s.updateDate < :theDate");
        query.setParameter("theDate", cal.getTime());
        for (SearchQuery searchQuery : (List<SearchQuery>) query.getResultList()) {
            em.remove(searchQuery);
        }
        em.flush();
        em.clear();
        //Queries less than 2 weeks old
        cal.add(Calendar.DAY_OF_MONTH, -15);
        query = em.createQuery("select s from SearchQuery s where s.size < 20 and s.updateDate < :theDate");
        query.setParameter("theDate", cal.getTime());
        for (SearchQuery searchQuery : (List<SearchQuery>) query.getResultList()) {
            em.remove(searchQuery);
        }
        em.flush();
        em.clear();
        //Queries more than 1 month old
        cal.add(Calendar.DAY_OF_MONTH, -30);
        query = em.createQuery("select s from SearchQuery s where s.updateDate < :theDate");
        query.setParameter("theDate", cal.getTime());
        for (SearchQuery searchQuery : (List<SearchQuery>) query.getResultList()) {
            em.remove(searchQuery);
        }
    }
}
