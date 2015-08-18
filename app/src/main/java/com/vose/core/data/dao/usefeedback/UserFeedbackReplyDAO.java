package com.vose.core.data.dao.usefeedback;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.userfeedback.UserFeedbackReply;

/**
 * Created by jimmyhou on 1/1/15.
 */
public interface UserFeedbackReplyDAO {

    public void saveIntoDatabase(UserFeedbackReply userFeedbackReply);

    public UserFeedbackReply getUserFeedbackReplyByUser(ParseUser user) throws ParseException;
}
