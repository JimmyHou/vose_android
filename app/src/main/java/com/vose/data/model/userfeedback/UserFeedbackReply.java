package com.vose.data.model.userfeedback;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jimmyhou on 1/1/15.
 */

@ParseClassName("UserFeedbackReply")
public class UserFeedbackReply extends ParseObject {

    public ParseUser getUser(){
        return  getParseUser("user");
    }

    public void setUser(ParseUser user){
        put("user", user);
    }

    public void setMessage(String message){
        put("message", message);
    }

    public String getMessage(){
        return getString("message");
    }

    public boolean isDisplayed(){
        return getBoolean("is_displayed");
    }

    public void setIsDisplayed(boolean isDisplayed){
        put("is_displayed", isDisplayed);
    }

    public void setOriginalUserFeedback(UserFeedback userFeedback){
        put("original_feedback", userFeedback);
    }

    public UserFeedback getOriginalUserFeedback(){
        return (UserFeedback) get("original_feedback");
    }

}

