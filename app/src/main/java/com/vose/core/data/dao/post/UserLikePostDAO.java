package com.vose.core.data.dao.post;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.UserLikePost;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/7.
 */
public interface UserLikePostDAO {

    public void saveIntoDatabase(UserLikePost userLikePost);

    public void deleteInDatabase(UserLikePost userLikePost);

    public  List<UserLikePost> getMostRecentlyLikedPosts(ParseUser user, int numberOfRetunredRows) throws ParseException;

    public UserLikePost getUserLikePostByPostAndCurrentUser(Post post) throws ParseException;

}
