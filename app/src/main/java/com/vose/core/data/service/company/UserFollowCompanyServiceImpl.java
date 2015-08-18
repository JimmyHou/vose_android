package com.vose.core.data.service.company;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.company.CompanyDAO;
import com.vose.core.data.dao.company.UserFollowCompanyDAO;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.UserFollowCompany;
import com.vose.notification.PushNotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/10.
 */
public class UserFollowCompanyServiceImpl implements UserFollowCompanyService {

    private UserFollowCompanyDAO userFollowCompanyDAO;

    private CompanyDAO companyDAO;

    public UserFollowCompanyServiceImpl(UserFollowCompanyDAO userFollowCompanyDAO, CompanyDAO companyDAO){
        this.userFollowCompanyDAO = userFollowCompanyDAO;
        this.companyDAO = companyDAO;
    }
    @Override
    public void createUserFollowCompany(Company company){
        UserFollowCompany userFollowCompany = new UserFollowCompany();
        userFollowCompany.setCompany(company);
        userFollowCompany.setUser(ParseUser.getCurrentUser());
        userFollowCompany.setVisible(true);

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        userFollowCompany.setACL(acl);
        userFollowCompanyDAO.saveIntoDatabase( userFollowCompany);

        company.setNumberFollowers(company.getNumberFollowers()+1);

        companyDAO.saveIntoDatabase(company);

       // PushNotificationService.subscribeById(company.getObjectId());
    }

    @Override
    public ArrayList<String> getFollowingCompanyIdsByUser(ParseUser user) throws ParseException {
        List<UserFollowCompany> userFollowCompanies = userFollowCompanyDAO.getUserFollowCompaniesByUser(user);
        ArrayList<String> userFollowCompanyIds = new ArrayList<String>();
        for(UserFollowCompany userFollowCompany:userFollowCompanies){
            userFollowCompanyIds.add(userFollowCompany.getCompany().getObjectId());
        }
        return userFollowCompanyIds;
    }

    //Pointer field , company, in UserFollowCompany only has objectId, no other server data
    @Override
    public ArrayList<Company> getFollowingCompaniesByUser(ParseUser user) throws ParseException {
        ArrayList<String> userFollowCompanyIds = getFollowingCompanyIdsByUser(user);
        return (ArrayList<Company>) companyDAO.getCompaniesByObjectIds(userFollowCompanyIds);
    }

    @Override
    public boolean isFollowedByCurrentUser(Company company) throws ParseException {
        UserFollowCompany userFollowCompany = userFollowCompanyDAO.getUserFollowCompanyByCompanyAndCurrentUser(company);
        if(userFollowCompany!=null)
            return true;
        else
            return false;
    }

    @Override
    public void unfollowCompany(Company company) throws ParseException {

       UserFollowCompany userFollowCompany =  userFollowCompanyDAO.getUserFollowCompanyByCompanyAndCurrentUser(company);

        if(userFollowCompany!=null) {

           company.setNumberFollowers(company.getNumberFollowers()-1);
           companyDAO.saveIntoDatabase(company);

           PushNotificationService.unsubscribeById(company.getObjectId());
           userFollowCompany.deleteEventually();

       }
    }

}
