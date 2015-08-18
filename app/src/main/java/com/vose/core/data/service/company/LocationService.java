package com.vose.core.data.service.company;

import com.parse.ParseException;
import com.vose.data.model.company.Location;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/30.
 */
public interface LocationService {
    List<Location> getLocations() throws ParseException;
}
