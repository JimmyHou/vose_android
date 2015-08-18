package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.vose.data.model.company.CompanyBasedLocation;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/30.
 */
public interface CompanyBasedLocationDAO {
    //the CompanyBasedLocation entity here only has company
    public List<CompanyBasedLocation> getCompanyBasedLocationByLocationNamesAndIndustryCode(List<String> locationNames, String industryCode,int numberOfCompanies) throws ParseException;

    public void saveIntoDatabase(CompanyBasedLocation companyBasedLocation);
 }
