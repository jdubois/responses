package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.service.CompanyService;
import com.github.jdubois.responses.service.InstanceService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Repository
public class CompanyServiceImpl implements CompanyService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private InstanceService instanceService;

    @Secured("ROLE_SU")
    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        List<Company> companies = em.createQuery("select c from Company c").getResultList();
        return companies;
    }

    @Secured("ROLE_SU")
    @Transactional
    public void addCompany(Company company) {
        em.persist(company);
    }

    @Transactional(readOnly = true)
    public Company getCompany(int companyId) {
        Company company = this.em.find(Company.class, companyId);
        Hibernate.initialize(company.getInstances());
        return company;
    }

    @Transactional(readOnly = true)
    public Company getCompanyByInstanceID(int instanceId) {
        Instance instance = instanceService.getInstance(instanceId);
        Company company = instance.getCompany();
        Hibernate.initialize(company.getName());
        return company;
    }
}
