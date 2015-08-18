package com.vose.core.data.service.userfeedback;

import com.vose.data.model.userfeedback.UserFeedback;
import com.vose.data.model.userfeedback.UserFeedbackReply;

/**
 * Created by jimmyhou on 2014/10/24.
 */
public interface UserFeedbackService {
    public UserFeedback createNewFeedback(String message, UserFeedbackReply originalFeedback);
}
