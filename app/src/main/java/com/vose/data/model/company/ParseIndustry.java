package com.vose.data.model.company;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by jimmyhou on 2014/11/25.
 */
@ParseClassName("Industry")
public class ParseIndustry extends ParseObject {

    public String getName(){
        return  getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getIndustryCode(){
        return  getString("industry_code");
    }

    public void setIndustryCode(String industryCode){
        put("industry_code", industryCode);
    }

}
