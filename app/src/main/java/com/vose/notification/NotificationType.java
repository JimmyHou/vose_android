package com.vose.notification;

/**
 * Created by jimmyhou on 4/4/15.
 */
public enum NotificationType {

    NewPostInCompany("new_post_in_company"),
    NewCommentInPost("new_comment_in_post");


    private String code;

    NotificationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
