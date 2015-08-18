package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.dao.post.UserLikePostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.core.data.service.post.UserLikePostService;
import com.vose.core.data.service.post.UserLikePostServiceImpl;
import com.vose.data.model.post.Post;

import java.util.List;

/**
 * Created by jimmyhou on 1/3/15.
 */

public class GetMostRecentlyLikedPostsTask extends AsyncTask<ParseUser, Integer, List<Post>> {
    private UserLikePostService userLikePostService;
    private PostService postService;
    private final int NUMBER_LIKES = 500;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userLikePostService = new UserLikePostServiceImpl(new UserLikePostDAOImpl());
        postService = new PostServiceImpl(new PostDAOImpl());
    }

    @Override
    protected  List<Post> doInBackground(ParseUser... params) {
        try {
            //should us a property file to handle this
            List<String> postIds = userLikePostService.getMostRecentlyLikedPostIds(params[0], NUMBER_LIKES);

            return postService.getPostsByObjectIds(postIds);
        } catch (ParseException e) {
            Log.e("Failed to query most recently liked post by userId " + params[0].getObjectId(), e.toString());
        }
        return null;
    }
}

