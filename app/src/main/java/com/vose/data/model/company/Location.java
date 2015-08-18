package com.vose.data.model.company;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by jimmyhou on 2014/11/30.
 */

@ParseClassName("Location")
public class Location extends ParseObject {
    public String getName(){
        return  getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getLocationCode(){
        return  getString("location_code");
    }

    public void setLocationCode(String industryCode){
        put("location_code", industryCode);
    }

}
