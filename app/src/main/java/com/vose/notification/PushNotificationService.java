package com.vose.notification;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;
import com.vose.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jimmyhou on 4/4/15.
 */
public class PushNotificationService {


    static final String LOG_TAG = "PushNotificationService";

    public static void subscribeById(String id){

        //put prefix as subscribeId
        ParsePush.subscribeInBackground(buildParseChannelId(id), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) {
                    Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
                }
            }
        });
    }

    public static void unsubscribeById(String id){

        //subscribe this company
        ParsePush.unsubscribeInBackground(buildParseChannelId(id), new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e!=null) {
                    Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
                }
            }
        });
    }


    public static void subscribeByIds(List<String> ids){

        List<String> parseChannelIds = new ArrayList<String>();

        for(String id: ids){

            parseChannelIds.add(buildParseChannelId(id));
        }


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.addAllUnique(ParsePushNotificationConstants.CHANNELS, parseChannelIds);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) {
                    Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
                }
            }
        });

    }

    //prefix P for channel ID since parse doesn't subscribe channel with Id not start by letter
    static public String buildParseChannelId(String objectId){

        return "P"+objectId;
    }


    //should use cloud code
    public static void pushNewPostInCompanyNotificationByCloudCode(Company company, String message){

        // truncate message in cloud code server
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(ParsePushNotificationConstants.INDUSTRY_NAME, Utility.mapIndustryCodeToName(company.getIndustryCode()));
        params.put(ParsePushNotificationConstants.COMPANY_ID, company.getObjectId());
        params.put(ParsePushNotificationConstants.COMPANY_NAME, company.getName());
        params.put(ParsePushNotificationConstants.MESSAGE,message);
        params.put(ParsePushNotificationConstants.INSTALLATION_ID, ParseInstallation.getCurrentInstallation().getObjectId());
        params.put(ParsePushNotificationConstants.NOTIFICATION_TYPE, NotificationType.NewPostInCompany.getCode());

        ParseCloud.callFunctionInBackground(ParsePushNotificationConstants.PUSH_FUNCTION_NEW_POST_IN_COMPANY, params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {

            }
        });
    }

    public static void pushNewPostInCompanyNotificationByClient(final Company company, String message){

        String industryName = Utility.mapIndustryCodeToName(company.getIndustryCode());

        LinkedList<String> channels = new LinkedList<String>();
        //need to prefix P
        channels.add("P"+company.getObjectId());
        channels.add("P"+industryName);

        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo(ParsePushNotificationConstants.IN_APP, false);
        pushQuery.whereNotEqualTo(ParsePushNotificationConstants.INSTALLATION_ID, ParseInstallation.getCurrentInstallation().getObjectId());
        pushQuery.whereContainedIn(ParsePushNotificationConstants.CHANNELS, channels);

        ParsePush push = new ParsePush();
        // Notice we use setChannels not setChannel
        //push.setChannels(channels);

        message = company.getName()+" has a new post in "+industryName+" industry: "+message;
        if(message.length()>80){
            message = message.substring(0,77);
        }


        JSONObject dataPassed = new JSONObject();
        try {

            //if we want to pass extra data, need to put message in alert of the data
            // the same with cloud code otherwise by setMessage, the notification doesn't work
            dataPassed.put(ParsePushNotificationConstants.ALERT, message);
            dataPassed.put(ParsePushNotificationConstants.COMPANY_ID, company.getObjectId());
            dataPassed.put(ParsePushNotificationConstants.INDUSTRY_NAME, industryName);
            dataPassed.put(ParsePushNotificationConstants.NOTIFICATION_TYPE, NotificationType.NewPostInCompany.getCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        push.setQuery(pushQuery);
        push.setData(dataPassed);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Log.e("NewPostPushNotification","NewPostPushNotification succeeds!");
                }else{
                    Log.e("NewPostPushNotification","NewPostPushNotification failed at "+company.getObjectId()+"with installation:"+ ParseInstallation.getCurrentInstallation().getObjectId());
                }
            }
        });
    }

    //should use cloud code
    public static void pushNewCommentInPostNotification(Post post, String message){

        // truncate message in cloud code server
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(ParsePushNotificationConstants.POST_ID, post.getObjectId());
        params.put(ParsePushNotificationConstants.MESSAGE,message);
        params.put(ParsePushNotificationConstants.INSTALLATION_ID, ParseInstallation.getCurrentInstallation().getObjectId());
        params.put(ParsePushNotificationConstants.NOTIFICATION_TYPE, NotificationType.NewCommentInPost.getCode());

        ParseCloud.callFunctionInBackground(ParsePushNotificationConstants.PUSH_FUNCTION_NEW_COMMENT_IN_POST, params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {

            }
        });
    }

}
