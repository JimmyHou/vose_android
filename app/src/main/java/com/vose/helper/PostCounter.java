package com.vose.helper;

import android.util.Log;

import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.vose.util.ParseDatabaseColumnNames;

/**
 * Created by jimmyhou on 1/28/15.
 */
public class PostCounter {

    private static String LOG_TAG = "PostCounter";
    private long startTime;
    private int POST_LIMIT = 5;
    private long TIME_INTERVAL = 10*60*1000;
    private long TIME_BLOCK = 30*60*1000;
    private static PostCounter _sInstance;
    private int numberPosts;
    private boolean isBlocked;

    public static int trackTimeAndPosts(){

        long timeInterval = System.currentTimeMillis() - _sInstance.startTime;

        //restart to count
        if((!_sInstance.isBlocked && timeInterval > _sInstance.TIME_INTERVAL)
                ||(_sInstance.isBlocked &&  timeInterval >_sInstance.TIME_BLOCK)){

            _sInstance.isBlocked = false;
            _sInstance.numberPosts = 1;
            _sInstance.startTime = System.currentTimeMillis();

        }else{
            _sInstance.numberPosts++;
        }

        //block the user
        if(_sInstance.numberPosts == _sInstance.POST_LIMIT){
            _sInstance.isBlocked = true;
            _sInstance.startTime = System.currentTimeMillis();
        }

        return _sInstance.numberPosts;
    }

    public static int getPostsLimit(){

        return _sInstance.POST_LIMIT;
    }


    public static long getBlockTime(){

        return _sInstance.TIME_BLOCK;
    }





    public static synchronized PostCounter getInstance() {

        if (null == _sInstance){
            _sInstance = new PostCounter();
            _sInstance.startTime = System.currentTimeMillis();
            _sInstance.isBlocked = false;
            loadConfigParameters(_sInstance);
        }

        return _sInstance;
    }

    public void clear(){
        if(_sInstance != null) {

            _sInstance = null;
        }
    }

    private static void loadConfigParameters(final PostCounter _sInstance){

        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                if (e == null) {
                    Log.d("System Config", "Config was fetched from the server.");
                } else {
                    Log.e("System Config", "Failed to fetch. Using Cached Config.");
                    config = ParseConfig.getCurrentConfig();
                }

                // Get the message from config or fallback to default value
                _sInstance.POST_LIMIT= config.getInt(ParseDatabaseColumnNames.USER_POST_LIMIT);
                _sInstance.TIME_INTERVAL = config.getInt(ParseDatabaseColumnNames.USER_POTS_TIME_INTERVAL)*60*1000;
                _sInstance.TIME_BLOCK = config.getInt(ParseDatabaseColumnNames.USER_BLOACKED_POST_TIME)*60*1000;

            }
        });
    }
}
