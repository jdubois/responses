package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Company;

import java.util.List;

/**
 * @author Julien Dubois
 */
public interface CompanyService {

    List<Company> getAllCompanies();

    void addCompany(Company company);

    Company getCompany(int companyId);

    Company getCompanyByInstanceID(int instanceId);
}
