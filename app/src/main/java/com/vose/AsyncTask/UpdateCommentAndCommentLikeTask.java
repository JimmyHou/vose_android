package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.comment.CommentDAOImpl;
import com.vose.core.data.dao.comment.UserLikeCommentDAOImpl;
import com.vose.core.data.service.comment.CommentService;
import com.vose.core.data.service.comment.CommentServiceImpl;
import com.vose.core.data.service.comment.UserLikeCommentService;
import com.vose.core.data.service.comment.UserLikeCommentServiceImpl;
import com.vose.data.model.post.Comment;

/**
 * Created by jimmyhou on 2014/9/22.
 */
public class UpdateCommentAndCommentLikeTask extends AsyncTask<Comment, Integer, Void> {
    private CommentService commentService;
    private UserLikeCommentService userLikeCommentService;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        commentService = new CommentServiceImpl(new CommentDAOImpl());
        userLikeCommentService = new UserLikeCommentServiceImpl(new UserLikeCommentDAOImpl());
    }

    @Override
    protected Void doInBackground(Comment... params) {
        commentService.saveIntoDatabase(params[0]);
        userLikeCommentService.createUserLikeComment(params[0]);
        return null;
    }
}
