package com.vose.data.model.util;

import java.io.Serializable;

/**
 * Created by jimmyhou on 2014/10/4.
 */
public enum ComponentSource implements Serializable {

    SignupFragment("signup_view"),
    LoginFragment("login_view"),
    MainTabHostActivity("main_tabs_view"),
    SearchFragment("search_view"),
    HotCompaniesFragment("companies_view"),
    HotVoicesFragment("voices_view"),
    FavoriteCompaniesFragment("following_view"),
    PostCommentsFragment("post_comments_view"),
    CompanyPostsFragment("company_posts_view"),
    SettingsView("setting_view"),
    PostView("post_view"),
    MyPostsFragment("my_posts_view");



    private String parseCode;

    ComponentSource(String parseCode){
        this.parseCode = parseCode;
    }

    public String getParseCode() {
        return parseCode;
    }
}
