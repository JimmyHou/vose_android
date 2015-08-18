package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.UserFollowCompanyDAOImpl;
import com.vose.core.data.service.company.UserFollowCompanyService;
import com.vose.core.data.service.company.UserFollowCompanyServiceImpl;
import com.vose.data.model.company.Company;

/**
 * Created by jimmyhou on 2014/10/1.
 */

public class  UnfollowCompanyTask extends AsyncTask<Company, Integer, Void> {
    private UserFollowCompanyService userFollowCompanyService;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userFollowCompanyService = new UserFollowCompanyServiceImpl(new UserFollowCompanyDAOImpl(), new CompanyDAOImpl());
    }


    @Override
    protected Void doInBackground(Company... params) {
        try {
            userFollowCompanyService.unfollowCompany(params[0]);
        } catch (ParseException e) {
            Log.e("failed to unfollow company: " + params[0].getObjectId(), e.toString());

        }
        return  null;
    }


}
