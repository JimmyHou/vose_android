package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;

/**
 * Created by jimmyhou on 2014/9/21.
 */
public class CreateNewPostTask extends AsyncTask<String, Integer, Post> {
    private Company company;
    private PostService postService;

    public CreateNewPostTask(Company company){
        this.company = company;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
    }


    @Override
    protected Post doInBackground(String... params) {
       return postService.createNewPost(params[0], company);
    }

    @Override
    protected void onPostExecute(Post result) {
        super.onPostExecute(result);
    }


}
