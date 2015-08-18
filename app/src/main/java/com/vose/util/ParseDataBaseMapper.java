package com.vose.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jimmyhou on 2014/12/2.
 */
public class ParseDataBaseMapper {

    public static String parseLocationNamesToString(List<String> locationNames){
        if(Utility.listIsEmpty(locationNames))
            return null;

        StringBuilder sb = new StringBuilder();

        for(String locationName: locationNames){
            sb.append(locationName).append(Constants.LOCATION_NAME_COMMA);
        }

        return  sb.toString();
    }

    public static List<String> parseStringToLocationNames(String locationNamesString){
        if(Utility.stringIsEmpty(locationNamesString))
            return null;

        return Arrays.asList(locationNamesString.split(Constants.LOCATION_NAME_COMMA));

    }

}
