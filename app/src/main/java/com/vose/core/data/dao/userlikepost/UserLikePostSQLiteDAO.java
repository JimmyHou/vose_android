package com.vose.core.data.dao.userlikepost;

import com.vose.data.model.post.Post;
import com.vose.data.model.post.UserLikePost;

import java.text.ParseException;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/6.
 */
public interface UserLikePostSQLiteDAO {

    public UserLikePost createUserLikePost(Post post) throws ParseException;

    public void openSQLiteDB();

    public void closeSQLiteDB();

    public List<String> getMostRecentlyLikedPostIds(String userId, int numberOfRetunredRows) throws ParseException;

    public void closeCursor();

}
