package com.vose.backgroundService;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.parse.ParseUser;
import com.vose.AsyncTask.GetAllCompaniesTask;
import com.vose.AsyncTask.GetFollowingCompaniesByUserTask;
import com.vose.AsyncTask.GetHotCompaniesByIndustryCodeTask;
import com.vose.AsyncTask.GetHotVoicesByIndustryBeforeCreatedTimeTask;
import com.vose.AsyncTask.GetIndustriesTask;
import com.vose.AsyncTask.GetMostRecentlyLikedPostIdsTask;
import com.vose.util.Constants;
import com.vose.util.ParseDatabaseColumnNames;

import java.util.Date;

/**
 * Created by jimmyhou on 3/31/15.
 */
public class ReloadMainActivityDataService extends IntentService {

    final static String DEBUG_LOG = "ReloadMainActivityDataService";

    public ReloadMainActivityDataService() {
        super(DEBUG_LOG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        reloadData();
    }

    private void reloadData(){

        new GetFollowingCompaniesByUserTask().execute(ParseUser.getCurrentUser());

        //use get since need to make sure hot companies is loaded before the next activity is started
        new GetIndustriesTask(this).execute("");

        new GetMostRecentlyLikedPostIdsTask().execute(ParseUser.getCurrentUser());

        String industryCode = (String) ParseUser.getCurrentUser().get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);
        new GetHotCompaniesByIndustryCodeTask().execute(industryCode);

        new GetHotVoicesByIndustryBeforeCreatedTimeTask(new Date()).execute(industryCode);
        new GetAllCompaniesTask(this).execute();

       // LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.UPDATE_MAIN_ACTIVITY_INDUSTRY_NAVIGATOR));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.UPDATE_HOT_VOICES_VIEW));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.UPDATE_HOT_COMPANIES_VIEW));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.UPDATE_FOLLOWING_COMPANIES_VIEW));

    }
}
