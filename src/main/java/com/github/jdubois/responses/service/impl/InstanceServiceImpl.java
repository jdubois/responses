package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Role;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.security.SecurityUtil;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.exception.InstanceException;
import com.github.jdubois.responses.service.exception.ResponsesSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Julien Dubois
 */
@Repository
@SuppressWarnings("unchecked")
public class InstanceServiceImpl implements InstanceService, ApplicationListener {

    private final Log log = LogFactory.getLog(InstanceServiceImpl.class);

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager em;

    private Map<String, Integer> instanceIndex;

    private Map<Integer, Long> instanceSize;

    @Transactional(readOnly = true)
    public Instance getInstance(int instanceId) {
        Instance instance = em.find(Instance.class, instanceId);
        if (instance == null) {
            throw new InstanceException("No instance with id " + instanceId);
        }
        if (instance.getType() == Instance.TYPE_PRIVATE) {
            User user = userService.getCurrentUser();
            if (user == null) {
                throw new ResponsesSecurityException("Anonymous user has tried to view instance "
                        + instance.getName());
            }
            if (!user.getInstances().contains(instance)) {
                if (!SecurityUtil.isUserInRole(Role.ROLE_SU)) {
                    throw new ResponsesSecurityException("User " + user.getId() + " has tried to view instance "
                            + instance.getName());
                }
            }
        }
        return instance;
    }

    @Transactional(readOnly = true)
    public Instance getInstanceByName(String instanceName) {
        Integer instanceId = instanceIndex.get(instanceName);
        if (instanceId == null) {
            throw new InstanceException("No instance with name " + instanceName);
        }
        return getInstance(instanceId);
    }

    public int findInstanceIdByName(String instanceName) {
        Integer id = instanceIndex.get(instanceName);
        if (id == null) {
            throw new InstanceException("No instance with name " + instanceName);
        }
        return id;
    }

    @Transactional
    @Secured("ROLE_SU")
    public void addInstance(int companyId, Instance instance) {
        Company company = this.em.find(Company.class, companyId);
        instance.setCompany(company);
        em.persist(instance);
        List<User> users = userService.findAllSuperUsers();
        for (User user : users) {
            Set<Instance> instances = user.getInstances();
            instances.add(instance);
            user.setInstances(instances);
        }
        instanceIndex.put(instance.getName(), instance.getId());
        instanceSize.put(instance.getId(), Long.valueOf(0));
    }

    @Transactional
    @Secured("ROLE_SU")
    public void editInstance(int instanceId, String longName) {
        Instance instance = getInstance(instanceId);
        instance.setLongName(longName);
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public void editInstance(String instanceName, String instanceLongName, String instanceDescription) {
        Instance instance = getInstanceByName(instanceName);
        instance.setLongName(instanceLongName);
        instance.setDescription(instanceDescription);
    }

    @Transactional(readOnly = true)
    public Set<Instance> getInstancesForUser(User user) {
        return user.getInstances();
    }

    @Transactional(readOnly = true)
    @Secured("ROLE_ADMIN")
    public List<Instance> getAllInstances() {
        Query qAllInstances = em.createNamedQuery("Instance.findAllInstances");
        return qAllInstances.getResultList();
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public void addUsersToInstance(int[] userIds, int instanceId) {
        Instance instance = getInstance(instanceId);
        Company company = instance.getCompany();
        for (int userId : userIds) {
            User user = userService.findUserById(userId);
            if (user.getCompany().equals(company)) {
                Set<Instance> userInstances = user.getInstances();
                userInstances.add(instance);
                user.setInstances(userInstances);
            } else {
                log.error("[Security] user \"" + user + "\" cannot be added to instance " + instance);
            }
        }
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public void removeUsersFromInstance(int[] userIds, int instanceId) {
        Instance instance = getInstance(instanceId);
        Company company = instance.getCompany();
        for (int userId : userIds) {
            User user = userService.findUserById(userId);
            if (user.getCompany().equals(company)) {
                Set<Instance> userInstances = user.getInstances();
                userInstances.remove(instance);
                user.setInstances(userInstances);
            } else {
                log.error("[Security] user \"" + user + "\" cannot be removed from instance " + instance);
            }
        }
    }

    public long getInstanceSize(int instanceId) {
        return instanceSize.get(instanceId);
    }

    public synchronized void incrementInstanceSize(int instanceId) {
        long size = instanceSize.get(instanceId);
        instanceSize.put(instanceId, size + 1);
    }

    public synchronized void decrementInstanceSize(int instanceId) {
        long size = instanceSize.get(instanceId);
        instanceSize.put(instanceId, size - 1);
    }

    @Transactional(readOnly = true)
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent && instanceIndex == null) {
            instanceIndex = new ConcurrentHashMap<String, Integer>(200);
            instanceSize = new ConcurrentHashMap<Integer, Long>(200);
            List<Instance> instances = em.createQuery("select i from Instance i").getResultList();
            for (Instance instance : instances) {
                instanceIndex.put(instance.getName(), instance.getId());
                Query query = em.createQuery("select count(q) from Question q where q.instance.id = :instanceId");
                query.setParameter("instanceId", instance.getId());
                Long size = (Long) query.getSingleResult();
                instanceSize.put(instance.getId(), size);
            }
            if (log.isInfoEnabled()) {
                log.info("Loaded " + instanceIndex.size() + " instances in the instance cache.");
            }
        }
    }
}
