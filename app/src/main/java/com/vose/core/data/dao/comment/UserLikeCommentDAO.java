package com.vose.core.data.dao.comment;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.UserLikeComment;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/9.
 */
public interface UserLikeCommentDAO {
    public void saveIntoDatabase(UserLikeComment UserLikeComment);

    public List<UserLikeComment> getMostRecentlyLikedComments(ParseUser user, int numberOfRetunredRows) throws ParseException;

    public UserLikeComment getUserLikeCommentByCommentAndCurrentUser(Comment comment) throws ParseException;

}
