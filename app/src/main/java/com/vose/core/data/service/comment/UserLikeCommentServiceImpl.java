package com.vose.core.data.service.comment;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.comment.UserLikeCommentDAO;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.UserLikeComment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/9.
 */
public class UserLikeCommentServiceImpl implements UserLikeCommentService{
    private UserLikeCommentDAO userLikeCommentDAO;

    public UserLikeCommentServiceImpl(UserLikeCommentDAO userLikeCommentDAO){
        this.userLikeCommentDAO = userLikeCommentDAO;
    }
    @Override
    public void createUserLikeComment(Comment comment){
        UserLikeComment userLikeComment = new UserLikeComment();
        userLikeComment.setComment(comment);
        userLikeComment.setUser(ParseUser.getCurrentUser());

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        userLikeComment.setACL(acl);
        userLikeCommentDAO.saveIntoDatabase(userLikeComment);
    }

    @Override
    public List<String> getMostRecentlyLikedCommentIds(ParseUser user, int numberOfRetunredRows) throws ParseException {
        List<UserLikeComment> userLikeComments = userLikeCommentDAO.getMostRecentlyLikedComments(user, numberOfRetunredRows);
        List<String> likedCommentIds = new ArrayList<String>();
        for(UserLikeComment userLikeComment: userLikeComments){
            likedCommentIds.add(userLikeComment.getComment().getObjectId());
        }
        return likedCommentIds;
    }

    @Override
    public boolean isLikedByCurrentUser(Comment comment) throws ParseException {
        UserLikeComment userLikeComment = userLikeCommentDAO.getUserLikeCommentByCommentAndCurrentUser(comment);
        if(userLikeComment!=null)
            return true;
        else
            return false;
    }
}
