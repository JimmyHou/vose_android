package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.vose.data.model.company.CompanyBasedLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/12/2.
 */
public class CompanyBasedLocationDAOImpl implements  CompanyBasedLocationDAO {
    @Override
    public List<CompanyBasedLocation> getCompanyBasedLocationByLocationNamesAndIndustryCode(List<String> locationNames, String industryCode, int numberOfCompanies) throws ParseException {
        ParseQuery<CompanyBasedLocation> query = ParseQuery.getQuery("CompanyBasedLocation");
        List<String> keys = new ArrayList<String>();
        keys.add("company");

        query.selectKeys(keys);
        query.whereContainedIn("location_name", locationNames);
        query.whereEqualTo("industry_code", industryCode);
        query.whereEqualTo("visible", true);
        query.setLimit(numberOfCompanies);
        query.orderByDescending("company_name");

        return query.find();
    }

    @Override
    public void saveIntoDatabase(CompanyBasedLocation companyBasedLocation) {
        companyBasedLocation.saveInBackground();
    }
}
