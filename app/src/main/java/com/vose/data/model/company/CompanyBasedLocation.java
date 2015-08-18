package com.vose.data.model.company;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by jimmyhou on 2014/11/30.
 */

@ParseClassName("CompanyBasedLocation")
public class CompanyBasedLocation extends ParseObject {

    public Company getCompany() {
        return (Company)getParseObject("company");
    }

    public void setCompany(Company company){
        put("company", company);
    }

    public String getLocationName() {
        return getString("location_name");
    }

    public void setLocationName(String locationName){
        put("location_name", locationName);
    }

    public String getIndustryCode() {
        return getString("industry_code");
    }

    public void setIndustryCode(String industryCode){
        put("industry_code", industryCode);
    }

    public String getCompanyName() {
        return getString("company_name");
    }

    public void setCompanyName(String companyName){
        put("company_name", companyName);
    }

    public boolean getVisible(){return getBoolean("visible");}

    public void setVisible(Boolean visible){put("visible", visible);}


}