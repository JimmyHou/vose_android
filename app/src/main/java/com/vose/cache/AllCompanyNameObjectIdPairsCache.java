package com.vose.cache;

import com.vose.core.data.dao.company.CompanyNameObjectIdPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/12/13.
 */
public class AllCompanyNameObjectIdPairsCache {
    private static String LOG_TAG = "AllCompanyNameObjectIdPairs";
    List<CompanyNameObjectIdPair> allCompanyNameObjectIdPairs;
    //singleton instance var
    private static AllCompanyNameObjectIdPairsCache _sInstance;

    public AllCompanyNameObjectIdPairsCache(){
        //initialize
        this.allCompanyNameObjectIdPairs = new ArrayList<CompanyNameObjectIdPair>();
    }

    public void setAllCompanyNameObjectIdPairs(List<CompanyNameObjectIdPair> allCompanyNameObjectIdPairs){
        clear();
        this.allCompanyNameObjectIdPairs = allCompanyNameObjectIdPairs;
    }

    public List<CompanyNameObjectIdPair> getAllCompanyNameObjectIdPairs(){return allCompanyNameObjectIdPairs;}



    public static synchronized AllCompanyNameObjectIdPairsCache getInstance() {
        if (null == _sInstance){
            _sInstance = new AllCompanyNameObjectIdPairsCache();
        }
        return _sInstance;
    }


    //need to reset each time when new users login
    public void clear(){
        if(_sInstance!=null)
            _sInstance.allCompanyNameObjectIdPairs = new ArrayList<CompanyNameObjectIdPair>();
    }
}
