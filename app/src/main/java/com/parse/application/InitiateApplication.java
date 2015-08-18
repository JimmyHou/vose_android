package com.parse.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.CompanyBasedLocation;
import com.vose.data.model.company.Location;
import com.vose.data.model.company.ParseIndustry;
import com.vose.data.model.company.UserCreatedCompany;
import com.vose.data.model.company.UserFollowCompany;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.UserFlagPost;
import com.vose.data.model.post.UserLikeComment;
import com.vose.data.model.post.UserLikePost;
import com.vose.data.model.userfeedback.UserFeedback;
import com.vose.data.model.userfeedback.UserFeedbackReply;

/**
 * Created by jimmyhou on 2014/8/6.
 */
public class InitiateApplication extends Application{
    private static SharedPreferences preferences;
    @Override
    public void onCreate(){
        super.onCreate();
        //need to register all parseObjects before Parse.initialize, otherwise not casting exception will show
        ParseObject.registerSubclass(Company.class);
        ParseObject.registerSubclass(ParseIndustry.class);
        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(CompanyBasedLocation.class);
        ParseObject.registerSubclass(UserCreatedCompany.class);

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(UserLikePost.class);
        ParseObject.registerSubclass(UserLikeComment.class);
        ParseObject.registerSubclass(UserFollowCompany.class);
        ParseObject.registerSubclass(UserFeedback.class);
        ParseObject.registerSubclass(UserFeedbackReply.class);
        ParseObject.registerSubclass(UserFlagPost.class);

        ParseCrashReporting.enable(this);
        //initialize Parse API

        //local:
//        final String APP_ID = "njmiEO6bVdHczSXvi94LianRNy75BYWCCmffsCTG";
//        final String CLIENT_KEY = "TJWusITURlDOG4VxpeVCGEpgyqJKIsilrZpGL4sS";

        //stage:
        //final String APP_ID = "tMpxEB1yVlZVp9Wrpp4gsJ6gCU6d675yXE4kmnux";
        //final String CLIENT_KEY = "Zyg8FpBwSNWmJAGobdWjAMw6hORRDzJE2PXDJ8D8";

        //Production:
        final String APP_ID = "DgX8lvgu9bkaD1VkS7sObPLKtDqJeBzffAx9kson";
        final String CLIENT_KEY = "vWPvgy7xLaU45eshauY3VhEfHJpEZVSy6IBPPX4a";

        Parse.initialize(this, APP_ID, CLIENT_KEY);
        // Save the current Installation to Parse, if we remove the entity in Installation database,
        //the only way to recreate it is to reinstall the app...

        //whenever the app is reinstalled, new installation entity will be created
        //so we need to migrate the following channels to new installation...
        //with call back the otherwise it won't 100% execute at Parse backend...
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });

        //not sure about SharedPrefernces
        preferences = getSharedPreferences("main.java.com", Context.MODE_PRIVATE);

    }
}
