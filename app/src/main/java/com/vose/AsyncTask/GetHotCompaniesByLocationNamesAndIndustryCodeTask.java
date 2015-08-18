package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.cache.HotCompaniesCache;
import com.vose.core.data.dao.company.CompanyBasedLocationDAOImpl;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.service.company.CompanyBasedLocationService;
import com.vose.core.data.service.company.CompanyBasedLocationServiceImpl;
import com.vose.data.model.company.Company;

import java.util.List;

/**
 * Created by jimmyhou on 2014/12/2.
 */

  public class GetHotCompaniesByLocationNamesAndIndustryCodeTask extends AsyncTask<List<String>, Integer,List<Company>> {

    private CompanyBasedLocationService companyBasedLocationService;
    private String mIndustryCode;
    private int NUMBER_OF_COMPANIES = 100;

    public GetHotCompaniesByLocationNamesAndIndustryCodeTask(String mIndustryCode){
        super();
        this.mIndustryCode = mIndustryCode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        companyBasedLocationService = new CompanyBasedLocationServiceImpl(new CompanyBasedLocationDAOImpl(), new CompanyDAOImpl());
    }

    @Override
    protected List<Company> doInBackground(List<String>... params) {
        try {
            //only return 30 companies
            return companyBasedLocationService.getCompaniesByLocationNamesAndIndustryCode(params[0], mIndustryCode, NUMBER_OF_COMPANIES);
        } catch (ParseException e) {
            Log.d("ERROR:", "\"failed to get hot companies by industry, locations from DB", e);
        }

        return null;
    }
    @Override
    protected void onPostExecute(List<Company> result) {
        super.onPostExecute(result);
        HotCompaniesCache.getInstance().setHotCompanies(result);

    }
   }
