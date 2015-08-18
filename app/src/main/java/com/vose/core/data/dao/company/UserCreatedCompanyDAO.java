package com.vose.core.data.dao.company;

import com.vose.data.model.company.UserCreatedCompany;

/**
 * Created by jimmyhou on 12/24/14.
 */
public interface UserCreatedCompanyDAO {

    public void saveIntoDatabase(UserCreatedCompany createdCompany);
}
