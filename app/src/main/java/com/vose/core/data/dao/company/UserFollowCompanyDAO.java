package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.UserFollowCompany;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/10.
 */
public interface UserFollowCompanyDAO {
        public void saveIntoDatabase(UserFollowCompany userFollowCompany);

        public List<UserFollowCompany> getUserFollowCompaniesByUser(ParseUser user)  throws ParseException;

        public UserFollowCompany getUserFollowCompanyByCompanyAndCurrentUser(Company company) throws ParseException;

}
