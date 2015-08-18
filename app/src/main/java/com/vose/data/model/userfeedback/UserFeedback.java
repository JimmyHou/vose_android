package com.vose.data.model.userfeedback;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jimmyhou on 2014/10/24.
 */

  @ParseClassName("UserFeedbackBackup")
  public class UserFeedback extends ParseObject {

     public ParseUser getAuthor(){
            return  getParseUser("author");
        }

     public void setAuthor(ParseUser author){
            put("author", author);
        }

     public void setMessage(String message){
        put("message", message);
    }

     public String getMessage(){
        return getString("message");
    }

     public void setOSVersion(String osVersion){
            put("os_version", osVersion);
        }

     public String getOSVersion(){
            return getString("os_version");
        }

     public void setDeviceModel(String deviceModel){
        put("device_model", deviceModel);
    }

     public String getDeviceModel(){
        return getString("device_model");
    }

     public boolean isSolved(){
        return getBoolean("is_solved");
     }

     public void setIsSolved(boolean isSolved){
        put("is_solved", isSolved);
     }

     public void setOriginalReply(UserFeedbackReply reply){put("original_reply", reply);}

     public Object getOriginalReply(){ return get("original_reply");}

    }
