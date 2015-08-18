package com.vose.core.data.dao.post;

import com.vose.data.model.post.UserFlagPost;

/**
 * Created by jimmyhou on 2014/11/16.
 */
public class UserFlagPostDAOImpl implements UserFlagPostDAO {
    @Override
    public void saveIntoDatabase(UserFlagPost userFlagPost){
       userFlagPost.saveInBackground();
    }

}
