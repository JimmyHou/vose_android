package com.vose.core.data.service.company;

import com.parse.ParseException;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.CompanyBasedLocation;

import java.util.List;

/**
 * Created by jimmyhou on 2014/12/2.
 */
public interface CompanyBasedLocationService {
    public List<Company> getCompaniesByLocationNamesAndIndustryCode(List<String> locationNames, String industryCode, int numberOfCompanies) throws ParseException;

    public List<String> getCompanyIdsByLocationNamesAndIndustryCode(List<String> locationNames, String industryCode, int numberOfCompanies) throws ParseException;

    public CompanyBasedLocation  createCompanyBasedLocation(Company company, String locationName);

}
