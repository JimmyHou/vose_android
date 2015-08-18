package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.UserFollowCompany;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/10.
 */
public class UserFollowCompanyDAOImpl implements  UserFollowCompanyDAO{

    @Override
    public void saveIntoDatabase(UserFollowCompany userFollowCompany){
        userFollowCompany.saveInBackground();
    }

    @Override
    public List<UserFollowCompany> getUserFollowCompaniesByUser(ParseUser user)  throws ParseException {
        ParseQuery<UserFollowCompany> query = ParseQuery.getQuery("UserFollowCompany");
        query.whereEqualTo("user", user);
        query.whereEqualTo("is_visible", true);
        query.orderByDescending("createdAt");
        return query.find();
    }

    @Override
    public UserFollowCompany getUserFollowCompanyByCompanyAndCurrentUser(Company company) throws ParseException{
    ParseQuery<UserFollowCompany> query = ParseQuery.getQuery("UserFollowCompany");
        query.whereEqualTo("company", company);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("is_visible", true);
        query.setLimit(1);
        List<UserFollowCompany> userFollowCompanies = query.find();
        if(userFollowCompanies.size() == 1)
            return userFollowCompanies.get(0);
        else
            return null;
    }
}
