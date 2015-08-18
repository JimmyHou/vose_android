package com.vose.cache;

import com.vose.data.model.company.Company;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/12/17.
 */
public class AllCompaniesCache {

    private static String LOG_TAG = "AllCompaniesCache";
    List<Company> allCompanies;
    private static AllCompaniesCache _sInstance;

    public  AllCompaniesCache(){
        //initialize
        this.allCompanies = new ArrayList<Company>();
    }

    public void setAllCompanies(List<Company> allCompanies){
        clear();
        this.allCompanies = allCompanies;
    }

    public List<Company> getAllCompanies(){return allCompanies;}



    public static synchronized AllCompaniesCache getInstance() {
        if (null == _sInstance){
            _sInstance = new AllCompaniesCache();
        }
        return _sInstance;
    }


    //need to reset each time when new users login
    public void clear(){
        if(_sInstance!=null)
            _sInstance.allCompanies = null;
    }
}
