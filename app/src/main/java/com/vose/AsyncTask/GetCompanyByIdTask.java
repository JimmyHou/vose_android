package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.service.company.CompanyService;
import com.vose.core.data.service.company.CompanyServiceImpl;
import com.vose.data.model.company.Company;

/**
 * Created by jimmyhou on 2014/12/14.
 */

public class GetCompanyByIdTask extends AsyncTask<String, Integer, Company> {
    private CompanyService companyService;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        companyService = new CompanyServiceImpl(new CompanyDAOImpl());
    }

    @Override
    protected Company doInBackground(String... params) {
        try {
            //should us a property file to handle this
            return companyService.getCompanyByObjectId(params[0]);
        } catch (ParseException e) {
            Log.e("Failed to query company by objectId " + ParseUser.getCurrentUser().getObjectId(), e.toString());
        }
        return null;
    }
}