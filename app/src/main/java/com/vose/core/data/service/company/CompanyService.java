package com.vose.core.data.service.company;

import com.parse.ParseException;
import com.vose.core.data.dao.company.CompanyNameObjectIdPair;
import com.vose.data.model.company.Company;

import java.util.List;

/**
 * Created by jimmyhou on 2014/8/19.
 */
public interface CompanyService {

    public List<Company> getHotCompaniesByIndustryCode(int numberOfCompanies, String industryCode) throws ParseException;

    public List<Company> getAllCompanies() throws ParseException;

    public List<CompanyNameObjectIdPair> getAllCompanyNameObjectIdPairs() throws ParseException;

    public void createCompany(Company company);

    public Company getCompanyByObjectId(String objectId) throws ParseException;

    public List<Company> getCompaniesByObjectIds(List<String> companyIds) throws ParseException ;

    public void saveIntoDatabase(Company company);
}
