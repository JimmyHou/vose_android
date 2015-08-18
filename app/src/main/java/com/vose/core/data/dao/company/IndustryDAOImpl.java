package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.vose.data.model.company.ParseIndustry;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/27.
 */
public class IndustryDAOImpl implements IndustryDAO {
    @Override
    public List<ParseIndustry> getIndustries() throws ParseException {

        ParseQuery<ParseIndustry> query = ParseQuery.getQuery("Industry");

        query.whereEqualTo("visible", true);
        query.orderByAscending("name");
        return query.find();
    }
}
