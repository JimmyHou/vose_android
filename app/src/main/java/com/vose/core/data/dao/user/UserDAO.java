package com.vose.core.data.dao.user;

import com.parse.ParseUser;

/**
 * Created by jimmyhou on 2014/11/27.
 */
public interface UserDAO {
    public void saveIntoDatabase(ParseUser user);
}
