package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.vose.data.model.company.ParseIndustry;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/27.
 */
public interface IndustryDAO {
    List<ParseIndustry> getIndustries() throws ParseException;

}
