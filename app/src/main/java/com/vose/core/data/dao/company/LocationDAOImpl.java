package com.vose.core.data.dao.company;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.vose.data.model.company.Location;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/30.
 */
public class LocationDAOImpl implements  LocationDAO {


    @Override
    public List<Location> getLocations() throws ParseException {

        ParseQuery<Location> query = ParseQuery.getQuery("Location");

        query.whereEqualTo("visible", true);
        query.orderByAscending("name");
        return query.find();
    }
}
