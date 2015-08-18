package com.vose.core.data.dao.post;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.UserLikePost;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/7.
 */
public class UserLikePostDAOImpl implements UserLikePostDAO {
    @Override
    public void saveIntoDatabase(UserLikePost userLikePost){
        userLikePost.saveInBackground();
    }


    @Override
    public void deleteInDatabase(UserLikePost userLikePost){

        userLikePost.deleteInBackground();

    }
    @Override
    public List<UserLikePost> getMostRecentlyLikedPosts(ParseUser user, int numberOfRetunredRows) throws ParseException {
        ParseQuery<UserLikePost> query = ParseQuery.getQuery("UserLikePost");
        query.whereEqualTo("user", user);
        query.setLimit(numberOfRetunredRows);
        query.orderByDescending("createdAt");
        return query.find();
    }


    @Override
    public UserLikePost getUserLikePostByPostAndCurrentUser(Post post) throws ParseException {
        ParseQuery<UserLikePost> query = ParseQuery.getQuery("UserLikePost");
        query.whereEqualTo("post", post);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.setLimit(1);
        List<UserLikePost> userLikePosts = query.find();
        if(userLikePosts.size() == 1)
            return userLikePosts.get(0);
        else
            return null;
    }
}
