package com.vose.core.data.service.comment;

import com.parse.ParseException;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.Post;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/29.
 */
public interface CommentService {
    public Comment createNewComment(String message, Post parentPost);
    public void saveIntoDatabase(Comment comment);
    public int getNumberOfCommentsByParentPost(Post parentPost) throws ParseException;
    public List<Comment> getCommentsByParentPostAfterCreatedTime(Post parentPost, int numberOfRows, Date createdAfter) throws ParseException;

}
