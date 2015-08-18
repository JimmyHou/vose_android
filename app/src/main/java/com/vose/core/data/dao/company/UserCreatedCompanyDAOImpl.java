package com.vose.core.data.dao.company;

import com.vose.data.model.company.UserCreatedCompany;

/**
 * Created by jimmyhou on 12/24/14.
 */
public class UserCreatedCompanyDAOImpl implements UserCreatedCompanyDAO {

    @Override
    public void saveIntoDatabase(UserCreatedCompany createdCompany) {

        createdCompany.saveInBackground();

    }
}
