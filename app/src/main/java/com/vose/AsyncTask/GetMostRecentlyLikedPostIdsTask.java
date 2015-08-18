package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.cache.AlreadyLikedPostIdsCache;
import com.vose.core.data.dao.post.UserLikePostDAOImpl;
import com.vose.core.data.service.post.UserLikePostService;
import com.vose.core.data.service.post.UserLikePostServiceImpl;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/17.
 */
public class GetMostRecentlyLikedPostIdsTask extends AsyncTask<ParseUser, Integer, List<String>> {
    private UserLikePostService userLikePostService;
    private final int NUMBER_LIKES = 500;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userLikePostService = new UserLikePostServiceImpl(new UserLikePostDAOImpl());
    }

    @Override
    protected  List<String> doInBackground(ParseUser... params) {
        try {
            //should us a property file to handle this
            return userLikePostService.getMostRecentlyLikedPostIds(params[0], NUMBER_LIKES);
        } catch (ParseException e) {
            Log.e("Failed to query most recently liked post by userId " + params[0].getObjectId(), e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);
        AlreadyLikedPostIdsCache.getInstance().setAlreadyLikedPostIds(result);
    }
}
