package com.vose.core.data.service.post;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.core.data.dao.post.UserLikePostDAO;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.UserLikePost;
import com.vose.notification.PushNotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/7.
 */
public class UserLikePostServiceImpl implements UserLikePostService{

    private UserLikePostDAO userLikePostDAO;

    public UserLikePostServiceImpl(UserLikePostDAO userLikePostDAO){
        this.userLikePostDAO = userLikePostDAO;
    }
    @Override
    public void createUserLikePost(Post post){
        UserLikePost userLikePost = new UserLikePost();
        userLikePost.setPost(post);
        userLikePost.setUser(ParseUser.getCurrentUser());

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        userLikePost.setACL(acl);
        userLikePostDAO.saveIntoDatabase(userLikePost);

        PushNotificationService.subscribeById(post.getObjectId());
    }

    @Override
    public void deleteUserLikePost(Post post) throws ParseException {

        //always need to get the row first tha delete it
       UserLikePost userLikePost= userLikePostDAO.getUserLikePostByPostAndCurrentUser(post);
       userLikePostDAO.deleteInDatabase(userLikePost);

       PushNotificationService.unsubscribeById(post.getObjectId());
    }

    @Override
    public List<String> getMostRecentlyLikedPostIds(ParseUser user, int numberOfReturnedRows) throws ParseException {

        List<UserLikePost> userLikePosts = userLikePostDAO.getMostRecentlyLikedPosts(user, numberOfReturnedRows);
        List<String> likedPostIds = new ArrayList<String>();
        for(UserLikePost userLikePost: userLikePosts){
            likedPostIds.add(userLikePost.getPost().getObjectId());
        }

        return likedPostIds;
    }

    @Override
    public boolean isLikedByCurrentUser(Post post) throws ParseException {
        UserLikePost userLikePost = userLikePostDAO.getUserLikePostByPostAndCurrentUser(post);
        if(userLikePost!=null)
            return true;
        else
            return false;
    }

}
