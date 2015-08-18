package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.vose.data.model.company.Company;

import java.util.List;

/**
 * Created by jimmyhou on 2014/8/18.
 */
public interface CompanyDAO {
    List<Company> getHotCompaniesByIndustryCode(int numberOfCompanies, String industryCode) throws ParseException;
    List<Company> getAllCompanies() throws ParseException;
    void saveIntoDatabase(Company company);
    Company getCompanyByObjectId(String objectId) throws ParseException;
    List<Company> getCompaniesByObjectIds(List<String> objectIds) throws ParseException;
 }
