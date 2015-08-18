package com.vose.data.model.post;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jimmyhou on 2014/9/3.
 *
 * user like post event, record what posts the user has been liked
 */
@ParseClassName("UserLikePost")
public class UserLikePost extends ParseObject {
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
}
