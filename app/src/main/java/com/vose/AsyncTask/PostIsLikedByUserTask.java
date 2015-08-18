package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.post.UserLikePostDAOImpl;
import com.vose.core.data.service.post.UserLikePostService;
import com.vose.core.data.service.post.UserLikePostServiceImpl;
import com.vose.data.model.post.Post;

/**
 * Created by jimmyhou on 2014/9/22.
 */
public class PostIsLikedByUserTask extends AsyncTask<Post, Integer, Boolean> {
    private UserLikePostService userLikePostService;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userLikePostService = new UserLikePostServiceImpl(new UserLikePostDAOImpl());
    }

    @Override
    protected Boolean doInBackground(Post... params) {

        try {
            return userLikePostService.isLikedByCurrentUser(params[0]);
        } catch (ParseException e) {
            Log.e("failed to check the post is liked by user: "+ ParseUser.getCurrentUser().getObjectId(), e.toString());
        }
        return null;
    }

}
