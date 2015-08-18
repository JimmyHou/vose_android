package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.dao.post.UserLikePostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.core.data.service.post.UserLikePostService;
import com.vose.core.data.service.post.UserLikePostServiceImpl;
import com.vose.data.model.post.Post;
import com.vose.util.Utility;

/**
 * Created by jimmyhou on 2/5/15.
 */
public class UpdatePostAndDeletePostLikeTask extends AsyncTask<Post, Integer, Void> {

    private final String LOG_TAG = "UpdatePostAndDeletePostLikeTask";
    private PostService postService;
    private UserLikePostService userLikePostService;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
        userLikePostService = new UserLikePostServiceImpl(new UserLikePostDAOImpl());
    }

    @Override
    protected Void doInBackground(Post... params) {
        postService.saveIntoDatabase(params[0]);
        try {
            userLikePostService.deleteUserLikePost(params[0]);
        } catch (ParseException e) {

            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));

        }
        return null;
    }
}