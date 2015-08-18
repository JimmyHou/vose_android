package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.UserFollowCompanyDAOImpl;
import com.vose.core.data.service.company.UserFollowCompanyService;
import com.vose.core.data.service.company.UserFollowCompanyServiceImpl;
import com.vose.data.model.company.Company;

/**
 * Created by jimmyhou on 2014/9/30.
 */

public class CreateUserFollowCompanyTask extends AsyncTask<Company, Integer, Void> {
    private Company company;
    private UserFollowCompanyService userFollowCompanyService;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userFollowCompanyService = new UserFollowCompanyServiceImpl(new UserFollowCompanyDAOImpl(), new CompanyDAOImpl());
    }


    @Override
    protected Void doInBackground(Company... params) {
        userFollowCompanyService.createUserFollowCompany(params[0]);
        return null;
    }


}