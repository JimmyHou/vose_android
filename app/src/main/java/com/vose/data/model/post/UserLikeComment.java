package com.vose.data.model.post;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jimmyhou on 2014/9/9.
 */

@ParseClassName("UserLikeComment")
public class UserLikeComment extends ParseObject {
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public Comment getComment() {
        return (Comment)getParseObject("comment");
    }

    public void setComment(Comment comment){
        put("comment", comment);
    }
}
