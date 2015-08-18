package com.vose.data.model.post;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.vose.data.model.userfeedback.FlagReason;

/**
 * Created by jimmyhou on 2014/9/3.
 *
 * user flag post event, record what posts the user has flagged
 */
@ParseClassName("UserFlagPost")
public class UserFlagPost extends ParseObject {
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public Post getPost() {
        return (Post)getParseObject("post");
    }

    public void setPost(Post post){
        put("post", post);
    }

    public  void setFlagReason(FlagReason flagReason){
        put("flag_reason", flagReason.getCode());
    };

    public FlagReason getFlagReason(){
        String flagReasonCode = getString("flag_reason");
        return  FlagReason.get(flagReasonCode);
    }
}
