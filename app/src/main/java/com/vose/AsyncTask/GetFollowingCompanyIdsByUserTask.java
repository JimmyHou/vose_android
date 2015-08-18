package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.UserFollowCompanyDAOImpl;
import com.vose.core.data.service.company.UserFollowCompanyService;
import com.vose.core.data.service.company.UserFollowCompanyServiceImpl;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/27.
 */

public class GetFollowingCompanyIdsByUserTask extends AsyncTask<ParseUser, Integer, List<String>> {
    private UserFollowCompanyService userFollowCompanyService;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userFollowCompanyService = new UserFollowCompanyServiceImpl(new UserFollowCompanyDAOImpl(), new CompanyDAOImpl());
    }

    @Override
    protected List<String> doInBackground(ParseUser... params) {
        try {
            //should us a property file to handle this
            return userFollowCompanyService.getFollowingCompanyIdsByUser(params[0]);
        } catch (ParseException e) {
            Log.e("Failed to query following companies by userId " + params[0].getObjectId(), e.toString());
        }
        return null;
    }
}