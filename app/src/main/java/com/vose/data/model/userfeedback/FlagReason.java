package com.vose.data.model.userfeedback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/11/17.
 */
public enum FlagReason {

    NOT_RELATED("not_related", "Not related"),
    LOW_CREDIBILITY("low_cred", "Low credibility"),
    BULLY("bully", "Bullying"),
    InAPPROPRIATE("inappropriate", "Inappropriate"),
    SPAM("spam", "Spam");

    String code;
    String displayName;

    private FlagReason(String code, String displayName){
        this.code = code;
        this.displayName = displayName;
    }

    private static final Map<String, FlagReason> lookup = new HashMap<String, FlagReason>();
    static{
        for(FlagReason reason : FlagReason.values()){
            lookup.put(reason.getCode(),reason);
        }
    }



    public String getCode(){
        return code;
    }

    public String getDisplayName() {return displayName;}

    public static FlagReason get(String code){
        return lookup.get(code);
    }

}
