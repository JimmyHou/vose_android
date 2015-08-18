package com.vose.AsyncTask;

import android.os.AsyncTask;

import com.vose.core.data.dao.comment.CommentDAOImpl;
import com.vose.core.data.service.comment.CommentService;
import com.vose.core.data.service.comment.CommentServiceImpl;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.Comment;
/**
 * Created by jimmyhou on 2014/9/23.
 */
public class CreateNewCommentTask extends AsyncTask<String, Integer, Comment> {
    private Post parentPost;
    private CommentService commentService;

    public  CreateNewCommentTask(Post parentPost){
        this.parentPost = parentPost;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        commentService = new CommentServiceImpl(new CommentDAOImpl());
    }


    @Override
    protected Comment doInBackground(String... params) {
        return commentService.createNewComment(params[0], parentPost);
    }


}
