package com.vose.core.data.service.company;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.vose.core.data.dao.company.CompanyBasedLocationDAO;
import com.vose.core.data.dao.company.CompanyDAO;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.CompanyBasedLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/12/2.
 */
public class CompanyBasedLocationServiceImpl implements CompanyBasedLocationService {

    private CompanyBasedLocationDAO companyBasedLocationDAO;
    private CompanyDAO companyDAO;

    public CompanyBasedLocationServiceImpl(CompanyBasedLocationDAO companyBasedLocationDAO, CompanyDAO companyDAO){
        this.companyBasedLocationDAO = companyBasedLocationDAO;
        this.companyDAO = companyDAO;
    }

    @Override
    public List<Company> getCompaniesByLocationNamesAndIndustryCode(List<String> locationNames, String industryCode,int numberOfCompanies) throws ParseException {
       return  companyDAO.getCompaniesByObjectIds(getCompanyIdsByLocationNamesAndIndustryCode(locationNames, industryCode, numberOfCompanies));
    }

    @Override
    public List<String> getCompanyIdsByLocationNamesAndIndustryCode(List<String> locationNames, String industryCode, int numberOfCompanies) throws ParseException {

        List<CompanyBasedLocation> companyBaseLocations = companyBasedLocationDAO.getCompanyBasedLocationByLocationNamesAndIndustryCode(locationNames, industryCode, numberOfCompanies);
        if(companyBaseLocations == null || companyBaseLocations.size() == 0){
            return  null;
        }

        //remove duplicate company from this sorted list
        List<String> companyIds = new ArrayList<String>();

        Company previousCompany = companyBaseLocations.get(0).getCompany();
        companyIds.add(previousCompany.getObjectId());

        for(CompanyBasedLocation companyBasedLocation : companyBaseLocations){
            Company currentCompany = companyBasedLocation.getCompany();
            if(!currentCompany.getObjectId().equals(previousCompany.getObjectId())) {
                companyIds.add(currentCompany.getObjectId());
                previousCompany = currentCompany;
            }
        }

        return companyIds;
    }

    @Override
    public CompanyBasedLocation createCompanyBasedLocation(Company company, String locationName) {
        CompanyBasedLocation companyBasedLocation = new CompanyBasedLocation();

        companyBasedLocation.setCompany(company);
        companyBasedLocation.setCompanyName(company.getName());
        companyBasedLocation.setLocationName(locationName);
        companyBasedLocation.setIndustryCode(company.getIndustryCode());

        companyBasedLocation.setVisible(true);

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        companyBasedLocation.setACL(acl);

        companyBasedLocationDAO.saveIntoDatabase(companyBasedLocation);


        return companyBasedLocation;
    }

}
