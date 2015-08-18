package com.vose.core.data.dao.company;

import com.vose.data.model.company.Company;

/**
 * Created by jimmyhou on 2014/12/14.
 */
public class CompanyNameObjectIdPair {

    private String objectId;
    private String companyName;


    public CompanyNameObjectIdPair(Company company){

        this.objectId = company.getObjectId();
        this.companyName = company.getName();
    }


    public String getObjectId(){
        return  objectId;
    }

    public String getCompanyName(){
        return  companyName;
    }


}
