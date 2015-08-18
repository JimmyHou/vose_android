package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.post.Post;
import com.vose.util.Utility;

/**
 * Created by jimmyhou on 1/1/15.
 */

public class GetPostByIdTask extends AsyncTask<String, Integer, Post> {

    final String LOG_TAG = "GetPostByIdTask";
    private PostService postService;


    @Override
    protected void onPreExecute() {
    super.onPreExecute();
    postService = new PostServiceImpl(new PostDAOImpl());
}

    @Override
    protected Post doInBackground(String... params) {
        try {
            return postService.getPostByObjectId(params[0]);
        } catch (ParseException e) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
        }
        return null;
    }
}