package com.vose.data.model.post;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.vose.data.model.company.Company;

import java.io.Serializable;

/**
 * Created by jimmyhou on 2014/8/16.
 */
@ParseClassName("Post")
public class Post extends ParseObject implements Serializable{
    public ParseUser getAuthor(){
        return  getParseUser("author");
    }

    public void setAuthor(ParseUser author){
        put("author", author);
    }

    //By default, when fetching an object, related ParseObjects are not fetched. These objects' values cannot be retrieved until they have been fetched like so:
    //get ParseObject won't return server data of ths relational object
    public Company getCompany(){
        return (Company) getParseObject("company");
    }

    //the returned company has sever data fetched in it
    public  Company getFetchedCompany() throws ParseException {
       return  getParseObject("company").fetch();
    }

    public void setCompany(Company company){
        put("company", company);
    }

    public String getIndustryCode(){
        return getString("industry");
    }

    public void setIndustryCode(String industryCode){
        put("industry", industryCode);
    }


    public String getCompanyName(){return getString("company_name");}

    public void setCompanyName(String companyName){put("company_name", companyName);}


    public void setMessage(String message){
        put("message", message);
    }

    public String getMessage(){
        return getString("message");
    }


    public int getNumberComments(){
        return getInt("number_comments");
    }

    public void setNumberComments(int numberComments){
        put("number_comments", numberComments);
    }

    public int getNumberLikes(){
        return getInt("number_likes");
    }

    public void setNumberLikes(int numberLikes){
        put("number_likes", numberLikes);
    }


    public int getNumberShares(){
        return getInt("number_shares");
    }

    public void setNumberShares(int numberShares){
        put("number_shares", numberShares);
    }

    public int getNumberFlagged(){
        return getInt("number_flagged");
    }

    public void setNumberFlagged(int numberFlagged){
        put("number_flagged", numberFlagged);
    }

    public Boolean getVisible(){return getBoolean("is_visible");}

    public void setVisible(Boolean visible){put("visible", visible);}



    public boolean isFlagged(){
        return getBoolean("is_flagged");
    }

    public void setFlagged(boolean isFlagged){
        put("is_flagged", isFlagged);
    }

}
