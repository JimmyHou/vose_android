package com.vose.cache;

import com.vose.data.model.company.Company;
import com.vose.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/11/8.
 */
public class FollowingCompaniesCache {
    private static String LOG_TAG = "FollowingCompaniesCache";
    //saved posts in memory
    List<Company> followingCompanies;
    //singleton instance var
    private static FollowingCompaniesCache _sInstance;

    public FollowingCompaniesCache(){
        //initialize
        this.followingCompanies = new ArrayList<Company>();
    }

    public void setFollowingCompanies(List<Company> followingCompanies){

        this.followingCompanies = followingCompanies;
    }

    public List<Company> getFollowingCompanies(){return followingCompanies;}



    public static synchronized FollowingCompaniesCache getInstance() {
        if (null == _sInstance){
            _sInstance = new FollowingCompaniesCache();
        }
        return _sInstance;
    }

    //need to reset each time when new users login
    public void clear(){
        _sInstance = null;
    }

    public boolean alreadyFollowThisCompany(Company company){

        if(Utility.listIsEmpty(_sInstance.followingCompanies)){
            return  false;
        }

        for(Company companyInList: _sInstance.followingCompanies) {
             if (companyInList.getObjectId().equals(company.getObjectId()))
                return true;
        }

        return false;
    }

    public Company getCompanyById(String companyId){

        if(companyId == null){

            return  null;
        }

        for(Company company : _sInstance.followingCompanies){

            if(company.getObjectId().equals(companyId)){

                return company;
            }
        }

        return null;
    }
}
