package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.post.Post;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 1/3/15.
 */

public class GetUserMadeAndLikedPostsTask extends AsyncTask<ParseUser, Integer,List<Post>> {

    private final String LOG_TAG = "GetUserMadeAndLikedPostsTask";
    private PostService postService;
    private final int NUMBER_POSTS = 500;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
    }

    @Override
    protected  List<Post> doInBackground(ParseUser... params) {
        try {

           return postService.getUserMadeAndLikedPosts(params[0], NUMBER_POSTS);

        } catch (ParseException e) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
        }
        return null;
    }
}

