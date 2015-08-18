package com.vose.cache;

import com.vose.data.model.company.Company;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/10/14.
 */
public class HotCompaniesCache {
    private static String LOG_TAG = "HotCompaniesCache";

    List<Company> hotCompanies;
    //singleton instance var
    private static HotCompaniesCache _sInstance;

    public HotCompaniesCache(){
        //initialize
        this.hotCompanies = new ArrayList<Company>();
    }

    public void setHotCompanies(List<Company> hotCompanies){
        this.hotCompanies = hotCompanies;
    }

    public List<Company> getHotCompanies(){return hotCompanies;}



    public static synchronized HotCompaniesCache getInstance() {
        if (null == _sInstance){
            _sInstance = new HotCompaniesCache();
        }
        return _sInstance;
    }


    //need to reset each time when new users login
    public void clear(){

        _sInstance = null;
    }
}
