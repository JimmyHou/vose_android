package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.comment.UserLikeCommentDAOImpl;
import com.vose.core.data.service.comment.UserLikeCommentService;
import com.vose.core.data.service.comment.UserLikeCommentServiceImpl;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/22.
 */
public class GetMostRecentlyLikedCommentIdsTask extends AsyncTask<ParseUser, Integer, List<String>> {
    private UserLikeCommentService userLikeCommentService;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userLikeCommentService = new UserLikeCommentServiceImpl(new UserLikeCommentDAOImpl());
    }

    @Override
    protected  List<String> doInBackground(ParseUser... params) {
        try {
            //should us a property file to handle this
            return userLikeCommentService.getMostRecentlyLikedCommentIds(params[0],100);
        } catch (ParseException e) {
            Log.e("Failed to query most recently liked post by userId " + params[0].getObjectId(), e.toString());
        }
        return null;
    }
}