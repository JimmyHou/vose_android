package com.vose.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/10/26.
 */
public class AlreadyLikedPostIdsCache {
    private static String LOG_TAG = "AlreadyLikedPostIdsChache";
    //saved posts in memory
    List<String> alreadyLikedPostIds;
    //singleton instance var
    private static AlreadyLikedPostIdsCache _sInstance;

    public AlreadyLikedPostIdsCache(){
        //initialize
        this.alreadyLikedPostIds = new ArrayList<String>();
    }

    public void setAlreadyLikedPostIds(List<String> alreadyLikedPostIds){
        this.alreadyLikedPostIds = alreadyLikedPostIds;}

    public List<String> getAlreadyLikedPostIds(){return alreadyLikedPostIds;}



    public static synchronized AlreadyLikedPostIdsCache getInstance() {
        if (null == _sInstance){
            _sInstance = new AlreadyLikedPostIdsCache();
        }
        return _sInstance;
    }

    //need to reset each time when new users login
    public void clear(){
        _sInstance = null;
    }
}

