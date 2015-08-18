package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.vose.data.model.company.Company;

import java.util.List;

/**
 * Created by jimmyhou on 2014/8/18.
 */
public class CompanyDAOImpl implements CompanyDAO {

    @Override
    public List<Company> getHotCompaniesByIndustryCode(int numberOfCompanies, String industryCode) throws ParseException {

        ParseQuery<Company> query = ParseQuery.getQuery("Company");
        if(industryCode !=null)
            query.whereEqualTo("industry", industryCode);
        query.setLimit(numberOfCompanies);
        query.orderByDescending("number_posts");
        return query.find();
    }

    @Override
    public List<Company> getAllCompanies() throws ParseException{
        ParseQuery<Company> query = ParseQuery.getQuery("Company");

        query.orderByAscending("name");
        //since parseQuery default limit is 100
        query.setLimit(100000);
        return query.find();
    }

    @Override
    public void saveIntoDatabase(Company company){
        company.saveInBackground();
    }

    @Override
    public Company getCompanyByObjectId(String objectId) throws ParseException {
        ParseQuery<Company> query = ParseQuery.getQuery("Company");
        query.setLimit(1);
        query.whereEqualTo("objectId", objectId);
        return query.find().get(0);
    }

    @Override
    public List<Company> getCompaniesByObjectIds(List<String> objectIds) throws ParseException {
        ParseQuery<Company> query = ParseQuery.getQuery("Company");
        query.whereContainedIn("objectId", objectIds);
        return query.find();
    }
}
