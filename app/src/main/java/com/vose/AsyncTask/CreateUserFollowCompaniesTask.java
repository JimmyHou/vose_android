package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.UserFollowCompanyDAOImpl;
import com.vose.core.data.service.company.UserFollowCompanyService;
import com.vose.core.data.service.company.UserFollowCompanyServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.notification.PushNotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 4/7/15.
 */

public class CreateUserFollowCompaniesTask extends AsyncTask<List<Company>, Integer, Void> {
    private List<Company> companies;
    private UserFollowCompanyService userFollowCompanyService;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userFollowCompanyService = new UserFollowCompanyServiceImpl(new UserFollowCompanyDAOImpl(), new CompanyDAOImpl());
    }


    @Override
    protected Void doInBackground(List<Company>... params) {

        companies = params[0];

        for(Company company: companies) {

            userFollowCompanyService.createUserFollowCompany(company);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){

        List<String> ids = new ArrayList<String>();
        for(Company company : companies){
            ids.add(company.getObjectId());
        }

        PushNotificationService.subscribeByIds(ids);
    }

}