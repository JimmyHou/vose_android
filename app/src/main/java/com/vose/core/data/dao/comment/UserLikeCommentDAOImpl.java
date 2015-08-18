package com.vose.core.data.dao.comment;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.UserLikeComment;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/9.
 */
public class UserLikeCommentDAOImpl implements UserLikeCommentDAO {

    @Override
    public void saveIntoDatabase(UserLikeComment userLikeComment){
        userLikeComment.saveInBackground();
    }

    @Override
    public List<UserLikeComment> getMostRecentlyLikedComments(ParseUser user, int numberOfRetunredRows) throws ParseException {
        ParseQuery<UserLikeComment> query = ParseQuery.getQuery("UserLikeComment");
        query.whereEqualTo("user", user);
        query.setLimit(numberOfRetunredRows);
        query.orderByDescending("createdAt");
        return query.find();
    }

    @Override
    public UserLikeComment getUserLikeCommentByCommentAndCurrentUser(Comment comment) throws ParseException {
        ParseQuery<UserLikeComment> query = ParseQuery.getQuery("UserLikeComment");
        query.whereEqualTo("comment", comment);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.setLimit(1);
        List<UserLikeComment> userLikeComments = query.find();
        if(userLikeComments.size() == 1)
            return userLikeComments.get(0);
        else
            return null;
    }

}
