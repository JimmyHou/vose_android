package com.vose.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.voice.app.R;
import com.vose.data.model.post.Post;

/**
 * Created by jimmyhou on 2014/11/18.
 */
public class IntentLauncher {


    public static void startSharePostEmailIntent(Context context, Post post){
        if(post == null)
            return;

        Utility.trackParseEvent(AnalyticsConstants.SHARE_POST, AnalyticsConstants.SHARE_MEANS, AnalyticsConstants.EMAIL, null);


        String emailContent = getShareContent(context, post);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("message/rfc882");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,context.getString(R.string.share_message_title)+" "+post.getCompanyName()+" !");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);
        context.startActivity(emailIntent);
    }


    public static void startSharePostSMSIntent(Context context, Post post){
        if(post == null)
            return;

        Utility.trackParseEvent(AnalyticsConstants.SHARE_POST, AnalyticsConstants.SHARE_MEANS, AnalyticsConstants.SMS, null);

        String SMSContent = getShareContent(context, post);

        Uri uri = Uri.parse("tel:123456");
        Intent SMSIntent = new Intent(Intent.ACTION_VIEW, uri);
        SMSIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        SMSIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SMSIntent.setType("vnd.android-dir/mms-sms");
        SMSIntent.putExtra("sms_body", SMSContent);
        context.startActivity(SMSIntent);
    }


    public static void startSharePostWhatsappIntent(Context context, Post post){
        if(post == null)
            return;

        Utility.trackParseEvent(AnalyticsConstants.SHARE_POST, AnalyticsConstants.SHARE_MEANS, AnalyticsConstants.WHATS_APP, null);

        String MessengerContent = getShareContent(context, post);
        Intent messengerIntent = new Intent();
        messengerIntent.setPackage("com.whatsapp");
        messengerIntent.setAction(Intent.ACTION_SEND);
        messengerIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        messengerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        messengerIntent.putExtra(Intent.EXTRA_TEXT, MessengerContent);
        messengerIntent.setType("text/plain");
        context.startActivity(messengerIntent);
    }


    public static void startSharePostMessengerIntent(Context context, Post post){
        if(post == null)
            return;

        Utility.trackParseEvent(AnalyticsConstants.SHARE_POST, AnalyticsConstants.SHARE_MEANS, AnalyticsConstants.OTHERS, null);

        String MessengerContent = getShareContent(context, post);
        Intent messengerIntent = new Intent();
        messengerIntent.setAction(Intent.ACTION_SEND);
        messengerIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        messengerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        messengerIntent.putExtra(Intent.EXTRA_TEXT, MessengerContent);
        messengerIntent.setType("text/plain");
        context.startActivity(messengerIntent);
    }



    private static String getShareContent(Context context, Post post){

//        String androidDownloadURL = SharedPreferencesManager.getInstance(context).getString(ParseDatabaseColumnNames.ANDROID_DOWNLOAD_URL, null);
//        String iosDownloadURL = SharedPreferencesManager.getInstance(context).getString(ParseDatabaseColumnNames.IOS_DOWNLOAD_URL, null);

        String androidDownloadURL = context.getString(R.string.android_download_url);
        String iosDownloadURL = context.getString(R.string.ios_download_url);

        String content = "About "+post.getCompanyName()+",\n"+"\n "+"\""+post.getMessage()+"\"";

        if(!Utility.isEmptyString(androidDownloadURL) || !Utility.isEmptyString(iosDownloadURL)){
            content = content +"\n"+"\n"+context.getString(R.string.share_get_voice_app);

            if(!Utility.isEmptyString(iosDownloadURL)){
                content = content+"\n"+"\n"+"iOS: "+iosDownloadURL;
            }


            if(!Utility.isEmptyString(androidDownloadURL)){
                content = content+"\n"+"\n"+"Android: "+androidDownloadURL;
            }
        }

        return  content;
    }
}
