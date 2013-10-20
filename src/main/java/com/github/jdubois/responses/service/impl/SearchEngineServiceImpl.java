package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.SearchQuery;
import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.service.QuestionService;
import com.github.jdubois.responses.service.SearchEngineService;
import com.github.jdubois.responses.service.SearchQueryService;
import com.github.jdubois.responses.service.dto.SearchQuestionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Repository("searchEngineService")
public class SearchEngineServiceImpl implements SearchEngineService {

    private final Log log = LogFactory.getLog(SearchEngineService.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SearchQueryService searchQueryService;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public void rebuildLuceneIndex() {
        log.info("Rebuilding the Lucene index");
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            batchIndexEntity(fullTextEntityManager, Question.class, "Question");
            batchIndexEntity(fullTextEntityManager, Tag.class, "Tag");
            batchIndexEntity(fullTextEntityManager, SearchQuery.class, "SearchQuery");

            fullTextEntityManager.getSearchFactory().optimize();
            watch.stop();
            log.info("Lucene index rebuilt in " + watch.getTotalTimeSeconds() + " seconds.");
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Lucene index could not be rebuilt : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void batchIndexEntity(FullTextEntityManager fullTextEntityManager, Class clazz, String entity) {
        //See http://relation.to/Bloggers/HibernateSearch32FastIndexRebuild
        //when Hibernate Search 3.2 is released
        fullTextEntityManager.purgeAll(clazz);
        Session session = (Session) em.getDelegate();
        org.hibernate.Query query = session.createQuery("select e from " + entity + " e");
        query.setCacheMode(CacheMode.IGNORE);
        query.setFlushMode(FlushMode.MANUAL);
        Iterator i = query.iterate();
        int batch = 0;
        while (i.hasNext()) {
            Object o = i.next();
            fullTextEntityManager.index(o);
            if (batch % 50 == 0) {
                fullTextEntityManager.flushToIndexes();
                session.clear();
            }
            batch++;
        }
        fullTextEntityManager.flushToIndexes();
        fullTextEntityManager.clear();
        log.debug("Indexed " + batch + " " + entity + "s");
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Object[]> suggestQuestions(int instanceId, String query) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        Term term = new Term("text", query);
        Query luceneQuery = new PrefixQuery(term);
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, SearchQuery.class);
        fullTextQuery.enableFullTextFilter("instanceFilter").setParameter("instanceId", String.valueOf(instanceId));
        SortField sortField = new SortField("size", SortField.INT, true);
        Sort sortBySize = new Sort(sortField);
        fullTextQuery.setSort(sortBySize);
        fullTextQuery.setProjection("text");
        fullTextQuery.setMaxResults(15);

        return fullTextQuery.getResultList();
    }

    @Transactional
    public SearchQuestionResult searchQuestions(int instanceId, String query) {
        searchQueryService.addSearchQuery(instanceId, query);
        return this.searchQuestions(instanceId, query, false, SearchEngineService.SORT_BY_DEFAULT, 0);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public SearchQuestionResult searchQuestions(int instanceId, String query, boolean showNegativeQuestions, int sortBy, int index) {

        if (log.isDebugEnabled()) {
            log.debug("Searching : instance=" + instanceId + "|query=" + query + "|negativeQuestions=" +
                    showNegativeQuestions + "|sort=" + sortBy + "|index=" + index);
        }

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

        fullTextEntityManager.getSearchFactory();
        Analyzer analyzer = fullTextEntityManager.getSearchFactory().getAnalyzer(Question.class);
        String[] fields = {"title", "text", "answers.text", "tags.text"};
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_30, fields, analyzer);
        try {
            Query luceneQuery = parser.parse(query);

            Session session = (Session) em.getDelegate();
            FullTextSession fullTextSession = org.hibernate.search.Search.getFullTextSession(session);
            org.hibernate.search.FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, Question.class);

            fullTextQuery.enableFullTextFilter("instanceFilter").setParameter("instanceId", String.valueOf(instanceId));
            if (!showNegativeQuestions) {
                fullTextQuery.enableFullTextFilter("voteFilter");
            }
            if (sortBy == SearchEngineService.SORT_BY_VOTE) {
                SortField sortField = new SortField("votesSize", SortField.INT, true);
                Sort sort = new Sort(sortField);
                fullTextQuery.setSort(sort);
            } else if (sortBy == SearchEngineService.SORT_BY_CREATION_DATE) {
                SortField sortField = new SortField("creationDate", SortField.STRING, true);
                Sort sort = new Sort(sortField);
                fullTextQuery.setSort(sort);
            } else if (sortBy == SearchEngineService.SORT_BY_UPDATE_DATE) {
                SortField sortField = new SortField("updateDate", SortField.STRING, true);
                Sort sort = new Sort(sortField);
                fullTextQuery.setSort(sort);
            }
            fullTextQuery.setFirstResult(index * 20);
            fullTextQuery.setMaxResults(20);
            SearchQuestionResult results = new SearchQuestionResult();
            results.setResultSize(fullTextQuery.getResultSize());
            Iterator i = fullTextQuery.iterate();
            List<Question> questions = new ArrayList<Question>();
            while (i.hasNext()) {
                Question question = (Question) i.next();
                questions.add(question);
            }
            questionService.hydrateQuestions(questions);
            results.setQuestions(questions);
            return results;
        } catch (ParseException e) {
            log.warn("The query \"" + query + "\"could not be parsed : " + e.getMessage());
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            SearchQuestionResult emptyResults = new SearchQuestionResult();
            emptyResults.setQuestions(new ArrayList<Question>());
            return emptyResults;
        }
    }


    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Object[]> searchTags(int instanceId, String query) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

        Term term = new Term("text", query);
        Query luceneQuery = new PrefixQuery(term);

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Tag.class);

        fullTextQuery.enableFullTextFilter("instanceFilter").setParameter("instanceId", String.valueOf(instanceId));

        SortField sortField = new SortField("size", SortField.INT, true);
        Sort sortBySize = new Sort(sortField);
        fullTextQuery.setSort(sortBySize);
        fullTextQuery.setProjection("text");      // ajouter , "size" pour avoir la taille des tags
        fullTextQuery.setMaxResults(5);

        //This returns a list of objects, not a list of tags, because of the projection
        return fullTextQuery.getResultList();
    }
}
