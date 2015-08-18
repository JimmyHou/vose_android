package com.vose.data.model.post;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jimmyhou on 2014/8/16.
 */
@ParseClassName("Comment")
public class Comment extends ParseObject{
    public ParseUser getAuthor(){
        return  getParseUser("author");
    }

    public void setAuthor(ParseUser author){
        put("author", author);
    }

    public Post getParentPost(){
        return (Post) getParseObject("parent_post");
    }

    public void setParentPost(Post parentPost){
        put("parent_post", parentPost);
    }

    public void setMessage(String message){
        put("message", message);
    }

    public String getMessage(){
        return getString("message");
    }


    public int getNumberLikes(){
        return getInt("number_likes");
    }

    public void setNumberLikes(int numberLikes){
        put("number_likes", numberLikes);
    }

    public int getNumberDislikes(){
        return getInt("number_dislikes");
    }

    public void setNumberDislikes(int numberDislikes){
        put("number_dislikes", numberDislikes);
    }

    public boolean isFlagged(){
       return getBoolean("is_flagged");
    }

    public void setFlagged(Boolean isFlagged){
        put("is_flagged", isFlagged);
    }


}
