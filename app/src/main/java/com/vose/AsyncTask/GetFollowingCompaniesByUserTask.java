package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.UserFollowCompanyDAOImpl;
import com.vose.core.data.service.company.UserFollowCompanyService;
import com.vose.core.data.service.company.UserFollowCompanyServiceImpl;
import com.vose.data.model.company.Company;

import java.util.List;

/**
 * Created by jimmyhou on 2014/10/3.
 */

public class GetFollowingCompaniesByUserTask  extends AsyncTask<ParseUser, Integer, List<Company>> {
    private UserFollowCompanyService userFollowCompanyService;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userFollowCompanyService = new UserFollowCompanyServiceImpl(new UserFollowCompanyDAOImpl(), new CompanyDAOImpl());
    }

    @Override
    protected List<Company> doInBackground(ParseUser... params) {
        try {
            //should us a property file to handle this
            return userFollowCompanyService.getFollowingCompaniesByUser(params[0]);
        } catch (ParseException e) {
            Log.e("Failed to query following companies by userId " + params[0].getObjectId(), e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Company> result) {
        super.onPostExecute(result);
        FollowingCompaniesCache.getInstance().setFollowingCompanies(result);
    }

}