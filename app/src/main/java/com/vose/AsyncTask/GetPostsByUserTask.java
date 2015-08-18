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
 * Created by jimmyhou on 12/24/14.
 */

public class GetPostsByUserTask extends AsyncTask<ParseUser, Integer,List<Post>> {

    private final String LOG_TAG = "GetPostsByUserIdTask";


    private PostService postService;
    private List<Post> posts;
    private int NUMBER_OF_POST = 500;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
    }

    @Override
    protected List<Post> doInBackground(ParseUser... params) {
        try {

            posts = postService.getPostsByUser(params[0], NUMBER_OF_POST);
            return posts;

        } catch (ParseException e) {

            Log.d(LOG_TAG, Utility.getExceptionStackTrace(e), e);
        }

        return null;
    }

}
