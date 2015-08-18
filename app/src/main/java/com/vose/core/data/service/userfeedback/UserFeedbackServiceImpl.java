package com.vose.core.data.service.userfeedback;

import android.os.Build;

import com.parse.ParseACL;
import com.parse.ParseUser;
import com.vose.core.data.dao.usefeedback.UserFeedbackDAO;
import com.vose.data.model.userfeedback.UserFeedback;
import com.vose.data.model.userfeedback.UserFeedbackReply;

/**
 * Created by jimmyhou on 2014/10/24.
 */
public class UserFeedbackServiceImpl implements UserFeedbackService {
    private UserFeedbackDAO userFeedbackDAO;

    public UserFeedbackServiceImpl( UserFeedbackDAO userFeedbackDAO) {
        this.userFeedbackDAO = userFeedbackDAO;
    }


    @Override
    public UserFeedback createNewFeedback(String message, UserFeedbackReply originalFeedback ) {
        UserFeedback userFeedback = new UserFeedback();
        userFeedback.setMessage(message);
        userFeedback.setAuthor(ParseUser.getCurrentUser());
        userFeedback.setDeviceModel(Build.MODEL);
        userFeedback.setOSVersion("Android " + Build.VERSION.RELEASE);
        userFeedback.setIsSolved(false);
        //set up read-only access control list for permission
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        userFeedback.setACL(acl);


        if(originalFeedback!=null) {
            userFeedback.setOriginalReply(originalFeedback);
        }

        //store the object
        userFeedbackDAO.saveIntoDatabase(userFeedback);
        return userFeedback;
    }
}
