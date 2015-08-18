package com.vose.data.model.util;

/**
 * Created by jimmyhou on 1/28/15.
 */
public enum UserStatus {

    ACTIVE("a"),
    REVIEW("r"),
    BLOCKED("b");

    private String code;

    UserStatus(String code){
        this.code = code;
    }

    public String getCode(){
        return  code;
    }

}
