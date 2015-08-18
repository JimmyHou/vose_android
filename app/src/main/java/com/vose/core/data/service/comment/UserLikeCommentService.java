package com.vose.core.data.service.comment;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.post.Comment;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/9.
 */
public interface UserLikeCommentService {
    public void createUserLikeComment(Comment comment);

    public List<String> getMostRecentlyLikedCommentIds(ParseUser user, int numberOfRetunredRows) throws ParseException;

    public boolean isLikedByCurrentUser(Comment comment) throws ParseException;

}
