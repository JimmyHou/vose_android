package com.vose.core.data.service.company;

import com.parse.ParseException;
import com.vose.core.data.dao.company.LocationDAO;
import com.vose.data.model.company.Location;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/30.
 */
public class LocationServiceImpl implements LocationService {

    private LocationDAO locationDAO;

    public  LocationServiceImpl(LocationDAO locationDAO){
        this.locationDAO = locationDAO;
    }
    @Override
    public List<Location> getLocations() throws ParseException {
        return locationDAO.getLocations();
    }
}
