package com.vose.core.data.service.company;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.company.Company;

import java.util.ArrayList;

/**
 * Created by jimmyhou on 2014/9/10.
 */
public interface UserFollowCompanyService {
    public void createUserFollowCompany(Company company);

    public ArrayList<String> getFollowingCompanyIdsByUser(ParseUser user) throws ParseException;

    public ArrayList<Company> getFollowingCompaniesByUser(ParseUser user) throws ParseException;

    public boolean isFollowedByCurrentUser(Company company) throws ParseException;

    public void unfollowCompany(Company company) throws ParseException;

}
