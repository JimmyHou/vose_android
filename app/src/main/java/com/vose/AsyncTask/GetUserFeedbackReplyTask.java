package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.usefeedback.UserFeedbackReplyDAOImpl;
import com.vose.core.data.service.userfeedback.UserFeedbackReplyService;
import com.vose.core.data.service.userfeedback.UserFeedbackReplyServiceImpl;
import com.vose.data.model.userfeedback.UserFeedbackReply;
import com.vose.util.Utility;

/**
 * Created by jimmyhou on 1/1/15.
 */

public class GetUserFeedbackReplyTask extends AsyncTask<ParseUser, Integer, UserFeedbackReply> {

    final String LOG_TAG = "GetUserFeedbackReplyTsk";
    private UserFeedbackReplyService userFeedbackReplyService;
    private OnTaskCompleted listener;


    public GetUserFeedbackReplyTask(OnTaskCompleted listener) {
       this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userFeedbackReplyService = new UserFeedbackReplyServiceImpl(new UserFeedbackReplyDAOImpl());
    }

    @Override
    protected UserFeedbackReply doInBackground(ParseUser... params) {
        try {
            return userFeedbackReplyService.getUserFeedbackReplyByUser(params[0]);
        } catch (ParseException e) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
        }
        return null;
    }


    @Override
    protected void onPostExecute(UserFeedbackReply result) {

        listener.onTaskCompleted(result);
    }
}