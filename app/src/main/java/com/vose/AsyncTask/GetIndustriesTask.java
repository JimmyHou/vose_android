package com.vose.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.cache.IndustriesCache;
import com.vose.core.data.dao.company.IndustryDAOImpl;
import com.vose.core.data.service.company.IndustryService;
import com.vose.core.data.service.company.IndustryServiceImpl;
import com.vose.data.model.company.ParseIndustry;
import com.vose.util.Constants;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/27.
 */

public class GetIndustriesTask extends AsyncTask<String, Integer,List<ParseIndustry>> {

    private final String LOG_TAG = "GetIndustriesTask";

    private IndustryService industryService;
    private Context context;


    public GetIndustriesTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        industryService = new IndustryServiceImpl(new IndustryDAOImpl());
    }

    @Override
    protected List<ParseIndustry> doInBackground(String... params) {
        try {
            return industryService.getIndustries();
        } catch (ParseException e) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<ParseIndustry> result) {
        super.onPostExecute(result);
        IndustriesCache.getInstance().setIndustries(result);

        SharedPreferencesManager.getInstance(context).putStringList(Constants.INDUSTRY_NAMES, IndustriesCache.getInstance().getIndustryNames());
        SharedPreferencesManager.getInstance(context).putStringList(Constants.INDUSTRY_CODES, IndustriesCache.getInstance().getIndustryCodes());
    }

}