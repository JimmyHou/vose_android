package com.vose.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.cache.AllCompaniesCache;
import com.vose.cache.CompanyCheckTimeMapCache;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.service.company.CompanyService;
import com.vose.core.data.service.company.CompanyServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.util.Utility;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/12/17.
 */


public class GetAllCompaniesTask  extends AsyncTask<String, Integer,List<Company>> {
    private CompanyService companyService;
    private Context context;

    public GetAllCompaniesTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        companyService = new CompanyServiceImpl(new CompanyDAOImpl());
    }



    @Override
    protected List<Company> doInBackground(String... params) {
        try {

            return companyService.getAllCompanies();
        } catch (ParseException e) {
            Log.d("ERROR:", "failed to get hot companies by industry from DB", e);
        }

        return null;
    }
    @Override
    protected void onPostExecute(List<Company> result) {
        super.onPostExecute(result);
        AllCompaniesCache.getInstance().setAllCompanies(result);
        updateCompanyCheckTimeMap(result);

    }

    //for company list has new posts
    private void updateCompanyCheckTimeMap(List<Company> allCompanies){

        //cache it at local
        Map<String, Date> map = CompanyCheckTimeMapCache.getInstance(context).getCompanyCheckTimeMap();
        if(map == null){
            map = new HashMap<String, Date>();
        }

        if(!Utility.listIsEmpty(allCompanies)){

            for(Company company: allCompanies){

                Date checkoutTime = map.get(company.getObjectId());
                if(checkoutTime == null){
                    map.put(company.getObjectId(), new Date(0));
                }

            }
        }

        CompanyCheckTimeMapCache.getInstance(context).setCompanyCheckTimeMap(map);
    }

}
