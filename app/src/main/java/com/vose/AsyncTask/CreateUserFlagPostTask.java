package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.post.UserFlagPostDAOImpl;
import com.vose.core.data.service.post.UserFlagPostService;
import com.vose.core.data.service.post.UserFlagPostServiceImpl;
import com.vose.data.model.post.Post;
import com.vose.data.model.userfeedback.FlagReason;

/**
 * Created by jimmyhou on 2014/11/16.
 */

public class CreateUserFlagPostTask extends AsyncTask<Post, Integer, Void> {
    private UserFlagPostService userFlagPostService;
    private FlagReason flagReason;

    public CreateUserFlagPostTask(FlagReason flagReason){
        this.flagReason = flagReason;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userFlagPostService = new UserFlagPostServiceImpl(new UserFlagPostDAOImpl());
    }

    @Override
    protected Void doInBackground(Post... params) {
        userFlagPostService.createUserFlagPost(params[0], flagReason);
        return null;
    }
}
