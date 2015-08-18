package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.core.data.dao.comment.CommentDAOImpl;
import com.vose.core.data.service.comment.CommentService;
import com.vose.core.data.service.comment.CommentServiceImpl;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.Comment;


import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/23.
 */
public class GetCommentsByParentPostAfterCreatedTimeTask extends AsyncTask<Post, Integer,List<Comment>> {
    private CommentService commentService;
    private List<Comment> comments;
    private Date lastCommentCreatedTime;
    private final int NUMBERCOMMENTS = 500;



    public GetCommentsByParentPostAfterCreatedTimeTask(Date lastCommentCreatedTime){
        super();
        this.lastCommentCreatedTime = lastCommentCreatedTime;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        commentService = new CommentServiceImpl(new CommentDAOImpl());
    }

    @Override
    protected List<Comment> doInBackground(Post... params) {
        try {

            comments = commentService.getCommentsByParentPostAfterCreatedTime(params[0], NUMBERCOMMENTS , lastCommentCreatedTime);;
            return  comments;
        } catch (ParseException e) {
            Log.d("ERROR:", "failed to get parent post comments from DB, postId: "+params[0].getObjectId(), e);
        }
        return null;
    }

}
