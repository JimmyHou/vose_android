package com.vose.core.data.service.post;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.vose.core.data.dao.post.PostDAO;
import com.vose.core.data.dao.post.UserLikePostDAOImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;
import com.vose.notification.PushNotificationService;
import com.vose.util.Utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/27.
 */
public class PostServiceImpl implements  PostService{

    private PostDAO postDAO;
    private UserLikePostService userLikePostService;

    public PostServiceImpl(PostDAO postDAO) {

        this.postDAO = postDAO;
        userLikePostService = new UserLikePostServiceImpl(new UserLikePostDAOImpl());
    }

    @Override
    public Post createNewPost(final String message, final Company company) {
        final Post post = new Post();
        post.setVisible(true);
        post.setMessage(message);
        post.setAuthor(ParseUser.getCurrentUser());
        post.setCompany(company);
        post.setIndustryCode(company.getIndustryCode().trim());
        post.setCompanyName(company.getName());
        post.setNumberLikes(0);
        //post.setNumberDislikes(0);
        post.setNumberComments(0);
        //post.setFlagged(false);
        //set up read-only access control list for permission
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        post.setACL(acl);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e==null){

                    PushNotificationService.subscribeById(post.getObjectId());
                    //PushNotificationService.pushNewPostInCompanyNotificationByCloudCode(company, message);
                    PushNotificationService.pushNewPostInCompanyNotificationByClient(company,message);
                }
            }
        });

        return post;
    }

    @Override
    public void saveIntoDatabase(Post post){
        postDAO.saveIntoDatabase(post);
    }

    @Override
    public Post getPostByObjectId(String objectId) throws ParseException{
        return postDAO.getPostByObjectId(objectId);
    }

    @Override
    public List<Post> getPostsByObjectIds(List<String> objectIds) throws ParseException {
        return postDAO.getPostsByObjectIds(objectIds);
    }

    @Override
    public List<Post> getPostsByUser(ParseUser user, int numberOfPosts) throws ParseException {
        return postDAO.getPostsByUser(user, numberOfPosts);
    }

    @Override
    public List<Post> getHotPostsByIndustryBeforeCreatedTime(String industryCode, int numberOfHotPosts, Date createdBefore) throws ParseException {
        return postDAO.getHotPostsByIndustryBeforeCreatedTime(industryCode, numberOfHotPosts,createdBefore);
    }

    @Override
    public List<Post> getHotPostsByIndustrySubsortedByBeforeCreatedTime(String industryCode, int numberOfHotPosts, int subsortUnit, Date createdBefore) throws ParseException {

        List<Post> hotPosts = postDAO.getHotPostsByIndustryBeforeCreatedTime(industryCode, numberOfHotPosts, createdBefore);

        return subsortPostsByLikes(hotPosts, subsortUnit);
    }

    @Override
    public List<Post> getHotPostsByCompaniesBeforeCreatedTime(List<Company> companies, int numberOfHotPosts, Date createdBefore) throws ParseException {
        return postDAO.getHotPostsByCompaniesBeforeCreatedTime(companies, numberOfHotPosts,createdBefore);
    }

    @Override
    public List<Post> getHotPostsByCompaniesSubsortedByLikesBeforeCreatedTime(List<Company> companies, int numberOfHotPosts, int subsortUnit, Date createdBefore) throws ParseException {

        List<Post> hotPosts = postDAO.getHotPostsByCompaniesBeforeCreatedTime(companies, numberOfHotPosts, createdBefore);

        return subsortPostsByLikes(hotPosts, subsortUnit);
    }

    @Override
    public List<Post> getPostsByCompanyBeforeCreatedTime(Company company, int numberOfRows, Date createdBefore) throws ParseException {
        return  postDAO.getPostsByCompanyBeforeCreatedTime(company, numberOfRows, createdBefore);
    }

    @Override
    public List<Post> getUserMadeAndLikedPosts(ParseUser user, int numberOfPosts) throws ParseException {

        List<String> postIds = userLikePostService.getMostRecentlyLikedPostIds(user, numberOfPosts);

        List<Post> userLikedPosts = getPostsByObjectIds(postIds);

        List<Post> userMadePosts = getPostsByUser(user, numberOfPosts);

        List<Post> joinPosts = new LinkedList<Post>();

        if(!Utility.listIsEmpty(userMadePosts)) {
            joinPosts.addAll(userMadePosts);
        }

        if(!Utility.listIsEmpty(userLikedPosts)) {
            joinPosts.addAll(userLikedPosts);
        }


        if(Utility.listIsEmpty(joinPosts)){

            return null;

        }else if(joinPosts.size() == 1){

            return joinPosts;
        }

        //sort it by created time

        Collections.sort( joinPosts, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                if (post1.getCreatedAt() == post2.getCreatedAt()) {
                    return 0;
                }

                return post1.getCreatedAt().compareTo(post2.getCreatedAt()) > 0 ? -1 : 1;
            }

            @Override
            public boolean equals(Object object) {
                return false;
            }
        });

        List<Post> result = new LinkedList<Post>();

        //remove duplicate from sorted array
        Post previousPost = joinPosts.get(0);
        result.add(previousPost);

        for(Post post : joinPosts){
            if(!post.getObjectId().equals(previousPost.getObjectId())){
                result.add(post);
                previousPost = post;
            }
        }

        return result;
    }


    private List<Post> subsortPostsByLikes(List<Post> posts, int subsortUnit){



        List<Post> sortedPosts = new LinkedList<Post>();

        int j = 0;
        for(int i = 0; i < posts.size();i=i+subsortUnit){
            j = i+subsortUnit > posts.size()? posts.size(): i+subsortUnit;

            List<Post> sublist = posts.subList(i, j);
            Collections.sort(sublist, new Comparator<Post>() {
                @Override
                public int compare(Post post1, Post post2) {
                    if(post1.getNumberLikes() == post2.getNumberLikes()){
                        return 0;
                    }

                    return post1.getNumberLikes()>post2.getNumberLikes()?-1:1;
                }

                @Override
                public boolean equals(Object object) {
                    return false;
                }
            });

            sortedPosts.addAll(sublist);
        }

        return sortedPosts;

    }


}
