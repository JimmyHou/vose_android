package com.vose.cache;

import android.content.Context;

import com.vose.util.Constants;
import com.vose.util.SharedPreferencesManager;

import java.util.Date;
import java.util.Map;

/**
 * Created by jimmyhou on 4/23/15.
 */
public class CompanyCheckTimeMapCache {

    private static CompanyCheckTimeMapCache _instance;
    private Map<String, Date> companyCheckTimeMap;
    private Context context;


    public static synchronized CompanyCheckTimeMapCache getInstance(Context context){
        if(_instance == null){
            _instance = new CompanyCheckTimeMapCache();
            _instance.companyCheckTimeMap = SharedPreferencesManager.getInstance(context).getStringDateMap(Constants.COMPANY_CHECKOUT_TIME_MAP);
            _instance.context = context;
        }

        return _instance;
    }

    public static Map<String, Date> getCompanyCheckTimeMap(){

        return _instance.companyCheckTimeMap;
    }

    public static void setCompanyCheckTimeMap(Map<String, Date>  companyCheckTimeMap){

        _instance.companyCheckTimeMap = companyCheckTimeMap;
        SharedPreferencesManager.getInstance(_instance.context).putStringDateMap(Constants.COMPANY_CHECKOUT_TIME_MAP, companyCheckTimeMap);
    }

    public static void updateCompanyCheckTimeMap(String companyId){
        //update the company check out time for company list checkmark
        _instance.companyCheckTimeMap.put(companyId, new Date());
        SharedPreferencesManager.getInstance(_instance.context).updateStringDateMap(Constants.COMPANY_CHECKOUT_TIME_MAP, companyId, new Date());

    }

}
