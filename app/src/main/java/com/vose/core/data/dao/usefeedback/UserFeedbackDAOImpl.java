package com.vose.core.data.dao.usefeedback;

import com.vose.data.model.userfeedback.UserFeedback;

/**
 * Created by jimmyhou on 2014/10/24.
 */
public class UserFeedbackDAOImpl implements  UserFeedbackDAO{
    @Override
    public void saveIntoDatabase(UserFeedback userFeedback){
        userFeedback.saveInBackground();
    }
}
