package com.vose.core.data.service.user;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/27.
 */
public interface UserService {
    public void updateUserIndustryAndLocationNames(String interestedIndustryCode, List<String> locationNames);

    public void updateUserIndustryCode(String interestedIndustryCode);


    public String getUserInterestedIndustryName();
}
