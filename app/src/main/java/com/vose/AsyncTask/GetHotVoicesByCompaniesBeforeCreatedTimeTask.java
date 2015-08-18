package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.cache.HotVoicesCache;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/12/2.
 */


public class GetHotVoicesByCompaniesBeforeCreatedTimeTask extends AsyncTask<List<Company>, Integer,List<Post>> {
    private PostService postService;
    private Date lastPostCreatedTime;
    private final int NUMBERPOSTS = 500;
    private final int SUBSORTUNIT = 3;


    public GetHotVoicesByCompaniesBeforeCreatedTimeTask(Date lastPostCreatedTime){
        super();
        this.lastPostCreatedTime = lastPostCreatedTime;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
    }




    @Override
    protected List<Post> doInBackground(List<Company>... params) {
        try {

            return postService.getHotPostsByCompaniesSubsortedByLikesBeforeCreatedTime(params[0], NUMBERPOSTS, SUBSORTUNIT, lastPostCreatedTime);

            } catch (ParseException e) {
            Log.d("ERROR:", "failed to get hot voices from DB", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Post> result) {
        super.onPostExecute(result);
        HotVoicesCache.getInstance().setHotVoices(result);
    }

}