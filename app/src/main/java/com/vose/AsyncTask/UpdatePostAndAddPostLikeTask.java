package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.dao.post.UserLikePostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.core.data.service.post.UserLikePostService;
import com.vose.core.data.service.post.UserLikePostServiceImpl;
import com.vose.data.model.post.Post;

/**
 * Created by jimmyhou on 2014/9/17.
 */
public class UpdatePostAndAddPostLikeTask extends AsyncTask<Post, Integer, Void> {
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
        userLikePostService.createUserLikePost(params[0]);
        return null;
    }
}
