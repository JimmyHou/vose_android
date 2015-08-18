package com.vose.cache;

import com.vose.data.model.post.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/10/13.
 */
public class HotVoicesCache {
    private static String LOG_TAG = "HotVoicesCache";
    //saved posts in memory
    List<Post> hotVoices;
    //singleton instance var
    private static HotVoicesCache _sInstance;

    public HotVoicesCache(){
        //initialize
        this.hotVoices  = new ArrayList<Post>();
    }

    public void setHotVoices(List<Post> hotVoices){
        this.hotVoices = hotVoices;
    }

    public List<Post> getHotVoices(){return hotVoices;}



    public static synchronized HotVoicesCache getInstance() {
        if (null == _sInstance){
            _sInstance = new HotVoicesCache();
        }
        return _sInstance;
    }

    public void clear(){

        _sInstance = null;
    }
}
