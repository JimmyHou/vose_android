package com.vose.core.data.service.post;

import com.vose.data.model.post.Post;
import com.vose.data.model.userfeedback.FlagReason;

/**
 * Created by jimmyhou on 2014/11/16.
 */
public interface UserFlagPostService {
    public void createUserFlagPost(Post post, FlagReason flagReason);
}
