package com.vose.data.model.util;

/**
 * Created by jimmyhou on 12/25/14.
 */
public enum ExtraIndustry {


        ALL("All Companies", "all"),
        FOLLOWING("Following Companies","following"),
        OTHER("Other", "other");


        String name;
        String code;

        ExtraIndustry(String name, String code){
            this.name = name;
            this.code = code;
        }

        public String getCode(){return code;}

        public String getName(){return  name;}

        static public boolean contains(String code){

            if(code.equals(ALL.getCode()) || code.equals(FOLLOWING.getCode())){
               return true;
            }

            return false;
        }

}
