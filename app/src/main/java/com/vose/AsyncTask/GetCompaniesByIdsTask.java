package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.service.company.CompanyService;
import com.vose.core.data.service.company.CompanyServiceImpl;
import com.vose.data.model.company.Company;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/27.
 */

public class GetCompaniesByIdsTask extends AsyncTask<List<String>, Integer, List<Company>> {
    private CompanyService companyService;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        companyService = new CompanyServiceImpl(new CompanyDAOImpl());
    }

    @Override
    protected List<Company> doInBackground(List<String>... params) {
        try {
            //should us a property file to handle this
            return companyService.getCompaniesByObjectIds(params[0]);
        } catch (ParseException e) {
            Log.e("Failed to query following companies by userId " + ParseUser.getCurrentUser().getObjectId(), e.toString());
        }
        return null;
    }
}