package com.vose.data.model.company;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/8/14.
 */
public enum Industry {
    TECHNOLOGY("Technology","software"),
    LAW("Law","law"),
    FINANCE("Finance","finance"),
    MEDIA_ENTERTAINMENT("Media& Entertainment", "media_entertainment"),
    RETAIL("Retail", "retail");


    String name;
    String code;

    private static final Map<String, Industry> lookup = new HashMap<String, Industry>();
    static{
        for(Industry i : Industry.values()){
            lookup.put(i.getCode(),i);
        }
    }


    private Industry(String name,String code){

        this.name = name;
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public String getName(){return  name;}

    public static Industry get(String code){
        return lookup.get(code);
    }
}


