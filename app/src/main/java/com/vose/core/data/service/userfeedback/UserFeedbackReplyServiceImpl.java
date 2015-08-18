package com.vose.core.data.service.userfeedback;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.usefeedback.UserFeedbackReplyDAO;
import com.vose.data.model.userfeedback.UserFeedbackReply;

/**
 * Created by jimmyhou on 1/1/15.
 */
public class UserFeedbackReplyServiceImpl implements UserFeedbackReplyService {


    private UserFeedbackReplyDAO userFeedbackReplyDAO;

    public UserFeedbackReplyServiceImpl( UserFeedbackReplyDAO userFeedbackReplyDAO) {
        this.userFeedbackReplyDAO = userFeedbackReplyDAO;
    }


    @Override
    public void saveIntoDatabase(UserFeedbackReply userFeedbackReply) {

        userFeedbackReplyDAO.saveIntoDatabase(userFeedbackReply);
    }

    @Override
    public UserFeedbackReply getUserFeedbackReplyByUser(ParseUser user) throws ParseException {

        return userFeedbackReplyDAO.getUserFeedbackReplyByUser(user);

    }
}
