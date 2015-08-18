package com.vose.core.data.service.company;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.core.data.dao.company.CompanyDAO;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.CompanyNameObjectIdPair;
import com.vose.core.data.dao.company.UserFollowCompanyDAOImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.util.ExtraIndustry;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 2014/8/19.
 */
public class CompanyServiceImpl implements CompanyService {

    CompanyDAO companyDAO;
    UserFollowCompanyService userFollowCompanyService;
    public CompanyServiceImpl(CompanyDAO companyDAO){

        this.companyDAO = companyDAO;

        userFollowCompanyService = new UserFollowCompanyServiceImpl(new UserFollowCompanyDAOImpl(), new CompanyDAOImpl());
    }


    @Override
    public List<Company> getHotCompaniesByIndustryCode(int numberOfCompanies, String industryCode) throws ParseException {


        //handel industryCode is all & following company

        if(industryCode.equals(ExtraIndustry.ALL.getCode())){

            industryCode = null;
        }

        else if(industryCode.equals(ExtraIndustry.FOLLOWING.getCode())){

            List<Company> followingCompanies = FollowingCompaniesCache.getInstance().getFollowingCompanies();

            if(!Utility.listIsEmpty(followingCompanies)){

                return followingCompanies;

            }else{

                followingCompanies = userFollowCompanyService.getFollowingCompaniesByUser(ParseUser.getCurrentUser());

                FollowingCompaniesCache.getInstance().setFollowingCompanies(followingCompanies);

                return followingCompanies;

            }
        }


        return companyDAO.getHotCompaniesByIndustryCode(numberOfCompanies, industryCode);
    }

    @Override
    public List<Company> getAllCompanies() throws ParseException {
        return companyDAO.getAllCompanies();
    }

    @Override
    public List<CompanyNameObjectIdPair> getAllCompanyNameObjectIdPairs() throws ParseException {
        List<Company> allCompanies = getAllCompanies();

        //repopulate CompanyNameObjectIdPair list to save memory

        return Utility.convertCompaniesToCompanyNameObjectIdPairs(getAllCompanies());
    }

    @Override
    public void createCompany(Company company) {
        companyDAO.saveIntoDatabase(company);
    }

    @Override
    public Company getCompanyByObjectId(String objectId) throws ParseException {
        return companyDAO.getCompanyByObjectId(objectId);
    }

    @Override
    public List<Company> getCompaniesByObjectIds(List<String> companyIds) throws ParseException {
        return companyDAO.getCompaniesByObjectIds(companyIds);
    }

    @Override
    public void saveIntoDatabase(Company company) {
        companyDAO.saveIntoDatabase(company);
    }
}
