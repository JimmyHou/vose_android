package com.vose.core.data.dao.usefeedback;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vose.data.model.userfeedback.UserFeedbackReply;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 1/1/15.
 */
public class UserFeedbackReplyDAOImpl implements UserFeedbackReplyDAO{

    @Override
    public void saveIntoDatabase(UserFeedbackReply userFeedbackReply) {
        userFeedbackReply.saveInBackground();
    }

    @Override
    public UserFeedbackReply getUserFeedbackReplyByUser(ParseUser user) throws ParseException {

        ParseQuery<UserFeedbackReply> query = ParseQuery.getQuery("UserFeedbackReply");
        query.setLimit(1);
        query.whereEqualTo("user", user);
        query.whereEqualTo("is_displayed", false);
        query.orderByAscending("createdAt");

        List<UserFeedbackReply> replyList = query.find();
        if(Utility.listIsEmpty(replyList)){
            return  null;
        }

        return replyList.get(0);
    }
}
