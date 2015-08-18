package com.vose.cache;

import com.vose.data.model.company.ParseIndustry;
import com.vose.data.model.util.ExtraIndustry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/11/27.
 */
public class IndustriesCache {
    private static String LOG_TAG = "IndustriesCache";
    private List<ParseIndustry> industries;
    private List<String> industryNames;
    private List<String> industryCodes;
    //singleton instance var
    private static IndustriesCache _sInstance;

    public IndustriesCache(){
        //initialize
        this.industries  = new ArrayList<ParseIndustry>();
    }

    public void setIndustries(List<ParseIndustry> industries){

        //put extra all and following company to top
        List<ParseIndustry> newIndustries = new LinkedList<ParseIndustry>();

        for(ParseIndustry industry : industries){
            if(industry.getIndustryCode().equals(ExtraIndustry.ALL.getCode())){

                newIndustries.add(0, industry);

            }else if(industry.getIndustryCode().equals(ExtraIndustry.FOLLOWING.getCode())){

                newIndustries.add(1, industry);

            }else{

                newIndustries.add(industry);
            }
        }

        this.industries = newIndustries;

        setIndustryNamesAndCodes();
    }

    public List<ParseIndustry> getIndustries(){return industries;}

    public void setIndustryNamesAndCodes(){

         industryNames = new ArrayList<String>();
         industryCodes = new ArrayList<String>();

        for(ParseIndustry industry: industries){
            industryNames.add(industry.getName());
            industryCodes.add(industry.getIndustryCode());
        }

    }

    public List<String> getIndustryNames(){

        return industryNames;
    }

    public List<String> getIndustryCodes(){

        return industryCodes;
    }



    public static synchronized IndustriesCache getInstance() {
        if (null == _sInstance){
            _sInstance = new IndustriesCache();
        }
        return _sInstance;
    }

    public void clear(){
        if(_sInstance != null) {

            _sInstance.industries= new ArrayList<ParseIndustry>();
        }
    }
}
