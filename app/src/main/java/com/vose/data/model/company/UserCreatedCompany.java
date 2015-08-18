package com.vose.data.model.company;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Created by jimmyhou on 12/24/14.
 */

@ParseClassName("UserCreatedCompany")
public class UserCreatedCompany extends ParseObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private ParseUser author;
    private String name;
    private String location;
    private String industryCode;

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

    public String getLocation(){
        return getString("location");
    }

    public void setLocation(String location){
        put("location", location);
    }

    public String getIndustryCode(){
        return getString("industry_code");
    }

    public void setIndustryCode(String industryCoe){
        put("industry_code", industryCoe);
    }


}
