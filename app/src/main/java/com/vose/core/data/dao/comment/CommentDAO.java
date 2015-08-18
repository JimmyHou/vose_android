package com.vose.core.data.dao.comment;

import com.parse.ParseException;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.Post;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/23.
 */
public interface CommentDAO {
    public void saveIntoDatabase(Comment comment);
    public int getNumberOfCommentsByParentPost(Post parentPost) throws ParseException;
    public List<Comment> getCommentsByParentPostAfterCreatedTime(Post parentPost, int numberOfRows, Date createdAfter) throws ParseException;

}
