package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.cache.AllCompanyNameObjectIdPairsCache;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.CompanyNameObjectIdPair;
import com.vose.core.data.service.company.CompanyService;
import com.vose.core.data.service.company.CompanyServiceImpl;

import java.util.List;

/**
 * Created by jimmyhou on 2014/12/14.
 */
public class GetAllCompanyNameObjectIdsTask extends AsyncTask<String, Integer,List<CompanyNameObjectIdPair>> {

    private CompanyService companyService;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        companyService = new CompanyServiceImpl(new CompanyDAOImpl());
    }

    @Override
    protected List<CompanyNameObjectIdPair> doInBackground(String... params) {
        try {

            return companyService.getAllCompanyNameObjectIdPairs();

        } catch (ParseException e) {
            Log.d("ERROR:", "failed to get hot voices from DB", e);
        }

        return null;
    }
    @Override
    protected void onPostExecute(List<CompanyNameObjectIdPair> result) {
        super.onPostExecute(result);
        AllCompanyNameObjectIdPairsCache.getInstance().setAllCompanyNameObjectIdPairs(result);
    }
}
