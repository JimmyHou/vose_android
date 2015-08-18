package com.vose.notification;

import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;
import com.vose.activity.MainTabHostActivity;
import com.vose.fragment.postcomment.CompanyPostsFragment;
import com.vose.fragment.postcomment.PostCommentsFragment;
import com.vose.util.Constants;
import com.vose.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jimmyhou on 3/23/15.
 */
public class ParsePushNotificationReceiver extends ParsePushBroadcastReceiver {

    private final String LOG_TAG = "PushNotificationRecv";
    public static final String PARSE_DATA_KEY = "com.parse.Data";


    @Override
    protected  void onPushOpen(android.content.Context context, android.content.Intent intent){

        ParseAnalytics.trackAppOpenedInBackground(intent);

        JSONObject data = getDataFromIntent(intent);
        String notificationType = null;

        try {
            notificationType = data.getString(ParsePushNotificationConstants.NOTIFICATION_TYPE);
        } catch (JSONException e) {
            Log.e(LOG_TAG,Utility.getExceptionStackTrace(e));
        }

        //Push fragment tag/post id
        Intent i = new Intent(context, MainTabHostActivity.class);
        i.putExtras(intent.getExtras());
        i.putExtra(ParsePushNotificationConstants.IS_NOTIFICATION, true);
        //required to call startActivity() from outside of an Activity and also Intent.FLAG_ACTIVITY_CLEAR_TASK to remove the old activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        setPushFragment(notificationType, data, i);

        context.startActivity(i);
    }



    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            Log.e(LOG_TAG,Utility.getExceptionStackTrace(e));

        }
        return data;
    }


    private void setPushFragment(String notificationType, JSONObject data, Intent i){
        if(notificationType == null){
            return;
        }


        if(notificationType.equals(NotificationType.NewPostInCompany.getCode())){
            pushCompanyPostsFragment(data, i);
        }else if(notificationType.equals(NotificationType.NewCommentInPost.getCode())){
            pushPostCommentsFragment(data, i);
        }
    }

    private void pushCompanyPostsFragment(JSONObject data, Intent i){

        String companyId = null;
        try {
           companyId = data.getString(ParsePushNotificationConstants.COMPANY_ID);
        } catch (JSONException e) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
        }

        String industryName = null;

        try {
            industryName = data.getString(ParsePushNotificationConstants.INDUSTRY_NAME);
        } catch (JSONException e) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
        }

        i.putExtra(ParsePushNotificationConstants.COMPANY_ID, companyId);
        i.putExtra(ParsePushNotificationConstants.INDUSTRY_NAME, industryName);
        i.putExtra(Constants.START_FRAGMENT, CompanyPostsFragment.FRAGMENT_TAG);
    }

    private void pushPostCommentsFragment(JSONObject data, Intent i){

        String postId = null;
        try {
            postId = data.getString(ParsePushNotificationConstants.POST_ID);
        } catch (JSONException e) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
        }

        i.putExtra(ParsePushNotificationConstants.POST_ID,  postId);
        i.putExtra(Constants.START_FRAGMENT, PostCommentsFragment.FRAGMENT_TAG);
    }


}
