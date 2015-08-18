package com.vose.core.data.service.post;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.post.Post;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/7.
 */
public interface UserLikePostService {
    public void createUserLikePost(Post post);

    public void deleteUserLikePost(Post post) throws ParseException;

    public List<String> getMostRecentlyLikedPostIds(ParseUser user, int numberOfRetunredRows) throws ParseException;

    public boolean isLikedByCurrentUser(Post post) throws ParseException;
}
