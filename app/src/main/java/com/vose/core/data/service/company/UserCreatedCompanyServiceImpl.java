package com.vose.core.data.service.company;

import com.parse.ParseACL;
import com.parse.ParseUser;
import com.vose.core.data.dao.company.UserCreatedCompanyDAO;
import com.vose.data.model.company.UserCreatedCompany;

/**
 * Created by jimmyhou on 12/24/14.
 */
public class UserCreatedCompanyServiceImpl implements UserCreatedCompanyService{


    UserCreatedCompanyDAO userCreatedCompanyDAO;

    public UserCreatedCompanyServiceImpl( UserCreatedCompanyDAO userCreatedCompanyDAO){
        this.userCreatedCompanyDAO = userCreatedCompanyDAO;
    }


    @Override
    public void createCompany(String companyName, String location, String industryCode) {

        UserCreatedCompany createdCompany = new UserCreatedCompany();

        createdCompany.setName(companyName);
        createdCompany.setLocation(location);
        createdCompany.setIndustryCode(industryCode);
        createdCompany.setAuthor(ParseUser.getCurrentUser());


        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        createdCompany.setACL(acl);

        //store the object
        userCreatedCompanyDAO.saveIntoDatabase(createdCompany);
    }
}
