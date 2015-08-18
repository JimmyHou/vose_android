package com.vose.cache;

import com.parse.ParsePush;

/**
 * Created by jimmyhou on 3/15/15.
 */
public class ParsePushCache {

    public static ParsePushCache _sInstance;
    private ParsePush push;


    public ParsePushCache() {
        this.push = new ParsePush();

    }

    public static synchronized ParsePushCache getInstance() {

        if(_sInstance == null){

            _sInstance = new ParsePushCache();
        }

        return _sInstance;
    }
}
