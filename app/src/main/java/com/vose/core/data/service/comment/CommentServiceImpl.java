package com.vose.core.data.service.comment;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.comment.CommentDAO;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.Post;
import com.vose.notification.PushNotificationService;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/29.
 */



public class CommentServiceImpl implements  CommentService {
    private CommentDAO commentDAO;

   public CommentServiceImpl(CommentDAO commentDAO){
        this.commentDAO = commentDAO;
    }


    @Override
    public Comment createNewComment(String message, Post parentPost){
        //save teh data to Parse
        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setAuthor(ParseUser.getCurrentUser());
        comment.setNumberLikes(0);
        comment.setNumberLikes(0);
        comment.setFlagged(false);
        comment.setParentPost(parentPost);


        //set up read-only access control list for permission
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        comment.setACL(acl);

        //store the object
        commentDAO.saveIntoDatabase(comment);
        PushNotificationService.pushNewCommentInPostNotification(parentPost, message);
        PushNotificationService.subscribeById(parentPost.getObjectId());
        return comment;
    }
    @Override
    public void saveIntoDatabase(Comment comment){
        commentDAO.saveIntoDatabase(comment);
    }


    public int getNumberOfCommentsByParentPost(Post parentPost) throws ParseException {
        return commentDAO.getNumberOfCommentsByParentPost(parentPost);
    }

    @Override
    public List<Comment> getCommentsByParentPostAfterCreatedTime(Post parentPost, int numberOfRows, Date createdAfter) throws ParseException {
        return commentDAO.getCommentsByParentPostAfterCreatedTime(parentPost, numberOfRows, createdAfter);
    }


}
