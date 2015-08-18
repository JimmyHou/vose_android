package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.cache.HotVoicesCache;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostService;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.post.Post;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/18.
 */
public class GetHotVoicesByIndustryBeforeCreatedTimeTask extends AsyncTask<String, Integer,List<Post>> {

    public final static String LOG_TAG = "GetHotVoicesByIndustryBeforeCreatedTimeTask";

    private PostService postService;
    private Date lastPostCreatedTime;
    private int NUMBER_POSTS = 500;
    private final int SUBSORT_UNIT = 3;


    public GetHotVoicesByIndustryBeforeCreatedTimeTask(Date lastPostCreatedTime){
        super();
        this.lastPostCreatedTime = lastPostCreatedTime;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        postService = new PostServiceImpl(new PostDAOImpl());
    }




    @Override
    protected List<Post> doInBackground(String... params) {
        try {
            return postService.getHotPostsByIndustrySubsortedByBeforeCreatedTime(params[0], NUMBER_POSTS, SUBSORT_UNIT, lastPostCreatedTime);
        } catch (ParseException e) {
            Log.d(LOG_TAG, Utility.getExceptionStackTrace(e));
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Post> result) {
        super.onPostExecute(result);
        HotVoicesCache.getInstance().setHotVoices(result);
    }

}
