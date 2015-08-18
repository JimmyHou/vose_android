package com.vose.core.data.dao.comment;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.Post;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/23.
 */
public class CommentDAOImpl implements  CommentDAO {
    @Override
    public void saveIntoDatabase(Comment comment){
        comment.saveInBackground();
    }

    @Override
    public int getNumberOfCommentsByParentPost(Post parentPost) throws ParseException {
        ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo("parent_post", parentPost);
        return query.find().size();
    }

    @Override
    public List<Comment> getCommentsByParentPostAfterCreatedTime(Post parentPost, int numberOfRows, Date createdAfter) throws ParseException {
        ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo("parent_post", parentPost);
        query.orderByAscending("createdAt");
        query.setLimit(numberOfRows);
        query.whereGreaterThanOrEqualTo("createdAt", createdAfter);
        return query.find();
    }


}
