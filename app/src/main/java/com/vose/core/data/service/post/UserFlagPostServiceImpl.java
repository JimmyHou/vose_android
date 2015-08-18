package com.vose.core.data.service.post;

import com.parse.ParseACL;
import com.parse.ParseUser;
import com.vose.core.data.dao.post.UserFlagPostDAO;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.UserFlagPost;
import com.vose.data.model.userfeedback.FlagReason;

/**
 * Created by jimmyhou on 2014/11/16.
 */
public class UserFlagPostServiceImpl implements UserFlagPostService {
    private UserFlagPostDAO userFlagPostDAO;

    public UserFlagPostServiceImpl(UserFlagPostDAO userFlagPostDAO){
        this.userFlagPostDAO = userFlagPostDAO;
    }
    @Override
    public void createUserFlagPost(Post post, FlagReason flagReason){
        UserFlagPost userFlagPost = new UserFlagPost();
        userFlagPost.setPost(post);
        userFlagPost.setUser(ParseUser.getCurrentUser());
        userFlagPost.setFlagReason(flagReason);

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        userFlagPost.setACL(acl);
        userFlagPostDAO.saveIntoDatabase(userFlagPost);
    }
}
