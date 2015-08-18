package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.service.company.CompanyService;
import com.vose.core.data.service.company.CompanyServiceImpl;
import com.vose.data.model.company.Company;

/**
 * Created by jimmyhou on 2014/10/25.
 */

public class UpdateCompanyIntoDBTask extends AsyncTask<Company, Integer, Void> {
    private CompanyService companyService;



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        companyService = new CompanyServiceImpl(new CompanyDAOImpl());
    }


    @Override
    protected Void doInBackground(Company... params) {
        companyService.saveIntoDatabase(params[0]);
        return null;
    }
}
