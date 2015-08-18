package com.vose.data.model.company;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jimmyhou on 2014/8/13.
 *
 */

// fields:
//author, name, shortName, industry, description, HQOffice, numberFollowers, numberPosts

    // should implement Parcelable to get faster!!
@ParseClassName("Company")
public class Company extends ParseObject implements Serializable {
    private static final long serialVersionUID = 1L;

    //Not to use objectId since GSon multiple declaration issue
    //if we want to save this object in SharePreference
    private String companyObjectId;

    private ParseUser author;
    private String name;
    private Office HQOffice;
    private int numberFollowers;
    private int numberPosts;

    public Company() {
        super();
    }


    public String getCompanyObjectId() {
        return super.getObjectId();
    }

    public void setCompanyObjectId(String companyObjectId) {
        this.companyObjectId = companyObjectId;
    }



    public ParseUser getAuthor(){
        return  getParseUser("author");
    }
    public void setAuthor(ParseUser author){
        put("author", author);
    }

    public String getName(){
         return getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getShortName(){
        return getString("short_name");
    }

    public void setShortName(String shortName){
        put("short_name", shortName);
    }

    public String getIndustryCode(){
        return getString("industry");
    }

    public void setIndustryCode(String industryCoe){
        put("industry", industryCoe);
    }

    public String getDescription(){
        return getString("description");
    }

    public void setDescription(String description){
        put("description", description);
    }

    // pass in office entity! but still have to return ParseObject here
    public Office getHQOffice(){
        return (Office)getParseObject("HQ_office");
    }

    public void setHQOffice(Office HQOffice){
        put("HQ_office", HQOffice);
    }
    //should get return from parse??
    public int getNumberFollowers(){

        return getInt("number_followers");
    }

    public void setNumberFollowers(int numberFollowers){
        put("number_followers", numberFollowers);
    }

    public int getNumberPosts(){
        return getInt("number_posts");
    }

    public void setNumberPosts(int numberPosts){
        put("number_posts", numberPosts);
    }


    public Date getPostTime(){
        return getDate("post_time");
    }

    public void setPostTime(Date date){
        put("post_time", date);
    }



}
