package com.vose.cache;

import com.vose.data.model.post.Post;
import com.vose.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/12/10.
 */

// for hot voices use since the posts are stored too slow at Parse
public class NewCreatedPostsCache {

    private static String LOG_TAG = "NewCreatedPostsCache";
    List<Post> newCreatedPosts;
    Map<String, List<Post>> companiesNewPostsMap;
    private static NewCreatedPostsCache _sInstance;


    public void setNewCreatedPosts(List<Post> newCreatedPosts){
        this.newCreatedPosts = newCreatedPosts;
    }

    public List<Post> getNewCreatedPosts(){return newCreatedPosts;}

    public void setNewPostToCompany(String companyName, Post post){

        if(post!=null){

            List<Post> posts = companiesNewPostsMap.get(companyName);
            if(Utility.listIsEmpty(posts)){
                posts = new ArrayList<Post>();
            }

            posts.add(0, post);

            companiesNewPostsMap.put(companyName, posts);
        }
    }

    public List<Post> getPostsByCompany(String companyName){

        if(!Utility.isEmptyString(companyName)){

            return companiesNewPostsMap.get(companyName);
        }

        return  null;
    }



    public static synchronized NewCreatedPostsCache getInstance() {

        if (null == _sInstance){
            _sInstance = new NewCreatedPostsCache();
            _sInstance.newCreatedPosts = new ArrayList<Post>();
            _sInstance.companiesNewPostsMap = new HashMap<String, List<Post>>();

        }

        return _sInstance;
    }

    public void clear(){
        if(_sInstance != null) {

            _sInstance = null;
        }
    }

}
