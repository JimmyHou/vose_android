package com.vose.core.data.dao.post;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;
import com.vose.data.model.util.ExtraIndustry;
import com.vose.util.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/20.
 */
public class PostDAOImpl implements PostDAO {
    @Override
    public void saveIntoDatabase(Post post){
        post.saveInBackground();
    }

    @Override
    public Post getPostByObjectId(String objectId) throws ParseException {
        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.setLimit(1);
        query.whereEqualTo("objectId", objectId);
        return query.find().get(0);
    }

    @Override
    public List<Post> getPostsByObjectIds(List<String> objectIds) throws ParseException {

        if(Utility.listIsEmpty(objectIds)){
            return null;
        }

        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.whereContainedIn("objectId", objectIds);
        return query.find();
    }

    @Override
    public List<Post> getPostsByUser(ParseUser user, int numberOfPosts) throws ParseException {

        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.setLimit(numberOfPosts);
        query.whereEqualTo("author", user);
        query.orderByDescending("createdAt");
        return query.find();
    }

    @Override

    public List<Post> getHotPostsByIndustryBeforeCreatedTime(String industryCode, int numberOfHotPosts, Date createdBefore) throws ParseException {
        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        if(industryCode!=null && !Utility.isExtraIndustryCode(industryCode))
            query.whereEqualTo("industry", industryCode.trim());
        query.setLimit(numberOfHotPosts);
        query.whereEqualTo("visible", true);
        query.whereLessThanOrEqualTo("createdAt", createdBefore);
        //need to figure out the algorithm to sort posts later on
        query.orderByDescending("createdAt,number_likes,number_comments");
        return query.find();
    }

    @Override
    public List<Post> getHotPostsByCompaniesBeforeCreatedTime(List<Company> companies, int numberOfHotPosts, Date createdBefore) throws ParseException {

        if(Utility.listIsEmpty(companies)){
            return  new ArrayList<Post>();
        }


        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.whereContainedIn("company", companies);
        query.whereEqualTo("visible", true);
        query.orderByDescending("createdAt,number_likes,number_comments");
        query.setLimit(numberOfHotPosts);
        query.whereLessThanOrEqualTo("createdAt", createdBefore);
        return query.find();
    }

    @Override
    public List<Post> getPostsByCompanyBeforeCreatedTime(Company company, int numberOfRows, Date createdBefore) throws ParseException {
        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.whereEqualTo("company", company);
        query.whereEqualTo("visible", true);
        query.orderByDescending("createdAt,number_likes,number_comments");
        query.setLimit(numberOfRows);
        query.whereLessThanOrEqualTo("createdAt", createdBefore);
        return query.find();
    }

}
