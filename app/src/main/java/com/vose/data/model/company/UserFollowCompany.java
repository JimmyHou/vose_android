package com.vose.data.model.company;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jimmyhou on 2014/9/10.
 */
@    ParseClassName("UserFollowCompany")
public class UserFollowCompany extends ParseObject {
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public Company getCompany() {
        return (Company)getParseObject("company");
    }

    public void setCompany(Company company){
        put("company", company);
    }

    public boolean isVisible(){return getBoolean("is_visible");}

    public void setVisible(boolean isVisible) { put("is_visible", isVisible); }
}
