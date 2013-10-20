package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.ExpertService;
import com.github.jdubois.responses.service.InstanceService;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Repository("ExpertService")
public class ExpertServiceImpl implements ExpertService {

    @Autowired
    InstanceService instanceService;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<User> getExpertsForInstance(String instanceName) {
        Instance instance = instanceService.getInstanceByName(instanceName);
        Query query = em.createNamedQuery("ExpertService.getExperts");
        query.setParameter("instanceId", instance.getId());
        return (List<User>) query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<User> getExpertsForInstances(Collection<Instance> instances) {
        List instanceIds = new ArrayList();
        for (Instance instance : instances) {
            if (instance.getType() == Instance.TYPE_PRIVATE) {
                instanceIds.add(instance.getId());
            }
        }
        Query query = em.createNamedQuery("ExpertService.getExpertsForInstances");
        query.setParameter("instanceIds", instanceIds);
        return (List<User>) query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestions(Collection<Instance> instances, int wfState, int assignedUserId, int index) {
        Session hibernateSession = (Session) em.getDelegate();
        Criteria criteria = hibernateSession.createCriteria(Question.class);
        criteria.setFetchMode("questionVotes", FetchMode.SELECT);
        criteria.setFetchMode("tags", FetchMode.SELECT);

        if (instances.size() == 1) {
            Instance instance = instances.iterator().next();
            criteria.createCriteria("instance").add(Restrictions.eq("id", instance.getId()));
        } else {
            Collection<Integer> ids = new ArrayList<Integer>();
            for (Instance instance : instances) {
                if (instance.getType() == Instance.TYPE_PRIVATE) {
                    ids.add(instance.getId());
                }
            }
            criteria.createCriteria("instance").add(Restrictions.in("id", ids));
        }
        if (wfState > -1) {
            criteria.add(Restrictions.eq("wfState", wfState));
        } else {
            criteria.add(Restrictions.gt("wfState", 0));
        }
        if (assignedUserId > 0) {
            criteria.add(Restrictions.eq("wfAssignedUser", assignedUserId));
        }
        criteria.setFirstResult(index);
        criteria.setMaxResults(50);

        return criteria.list();
    }
}
