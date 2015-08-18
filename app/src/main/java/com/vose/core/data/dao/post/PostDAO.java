package com.vose.core.data.dao.post;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/20.
 */
public interface PostDAO {
    public void saveIntoDatabase(Post post);

    public Post getPostByObjectId(String objectId) throws ParseException;

    public List<Post> getPostsByObjectIds(List<String> objectIds) throws ParseException;

    public List<Post> getPostsByUser(ParseUser user, int numberOfPosts) throws ParseException;

    public List<Post> getHotPostsByIndustryBeforeCreatedTime(String industryCode, int numberOfHotPosts, Date createdBefore) throws ParseException;

    public List<Post> getHotPostsByCompaniesBeforeCreatedTime(List<Company> companies, int numberOfHotPosts, Date createdBefore) throws ParseException;

    public List<Post> getPostsByCompanyBeforeCreatedTime(Company company, int numberOfRows, Date createdBefore) throws ParseException;


}
