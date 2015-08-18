package com.vose.core.data.service.user;

import com.parse.ParseUser;
import com.vose.core.data.dao.user.UserDAO;
import com.vose.util.ParseDataBaseMapper;
import com.vose.util.ParseDatabaseColumnNames;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/27.
 */
public class UserServiceImpl implements UserService{

    private UserDAO userDAO;

    public  UserServiceImpl(UserDAO userDAO){
            this.userDAO = userDAO;
    }

    @Override
    public void updateUserIndustryAndLocationNames(String interestedIndustryCode, List<String> locationNames) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE, interestedIndustryCode);
        user.put(ParseDatabaseColumnNames.USER_INTERESTED_LOCATION_NAMES, ParseDataBaseMapper.parseLocationNamesToString(locationNames));
        userDAO.saveIntoDatabase(user);
    }

    @Override
    public void updateUserIndustryCode(String interestedIndustryCode) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE, interestedIndustryCode);
        userDAO.saveIntoDatabase(user);
    }

    @Override
    public String getUserInterestedIndustryName() {

        return (String) ParseUser.getCurrentUser().get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);
    }
}
