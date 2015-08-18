package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/20.
 */
public class GetPostsByCompanyBeforeCreatedTimeTask extends AsyncTask<Company, Integer,List<Post>> {

    private final String LOG_TAG = "GetPostsByCompanyBeforeCreatedTimeTask";

    private PostService postService;
    private List<Post> posts;
    private Date lastPostCreatedTime;
    private int NUMBER_OF_POST = 500;

    public GetPostsByCompanyBeforeCreatedTimeTask(Date lastPostCreatedTime){
        super();
        this.lastPostCreatedTime = lastPostCreatedTime;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
    }

    @Override
    protected List<Post> doInBackground(Company... params) {
        try {
            posts = postService.getPostsByCompanyBeforeCreatedTime(params[0], NUMBER_OF_POST, lastPostCreatedTime);;
            return posts;
        } catch (ParseException e) {
            Log.d(LOG_TAG, Utility.getExceptionStackTrace(e), e);
        }

        return null;
    }

}
