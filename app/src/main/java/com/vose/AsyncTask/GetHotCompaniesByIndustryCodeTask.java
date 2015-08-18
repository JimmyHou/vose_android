package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.cache.HotCompaniesCache;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.service.company.CompanyService;
import com.vose.core.data.service.company.CompanyServiceImpl;
import com.vose.data.model.company.Company;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/23.
 */
public class GetHotCompaniesByIndustryCodeTask extends AsyncTask<String, Integer,List<Company>> {
    private CompanyService companyService;
    private int NUMBER_OF_COMPANIES = 30;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        companyService = new CompanyServiceImpl(new CompanyDAOImpl());
    }

    @Override
    protected List<Company> doInBackground(String... params) {
        try {
            //only return 30 companies
            return companyService.getHotCompaniesByIndustryCode(NUMBER_OF_COMPANIES, params[0]);
        } catch (ParseException e) {
            Log.d("ERROR:", "failed to get hot companies by industry from DB", e);
        }

        return null;
    }
    @Override
    protected void onPostExecute(List<Company> result) {
        super.onPostExecute(result);
        HotCompaniesCache.getInstance().setHotCompanies(result);
    }

}