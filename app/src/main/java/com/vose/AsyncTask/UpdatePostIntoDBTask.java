package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.post.Post;

/**
 * Created by jimmyhou on 2014/9/23.
 */
public class UpdatePostIntoDBTask extends AsyncTask<Post, Integer, Void> {
    private PostService postService;



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
    }


    @Override
    protected Void doInBackground(Post... params) {
        postService.saveIntoDatabase(params[0]);
        return null;
    }
}
