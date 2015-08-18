package com.vose.cache;

import com.vose.data.model.company.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/11/30.
 */
public class LocationsCache {
    private static String LOG_TAG = "LocationsCache";
    private List<Location> locations;
    private List<String> selectedLocationNames;
    //singleton instance var
    private static LocationsCache _sInstance;

    public LocationsCache(){
        //initialize
        this.locations = new ArrayList<Location>();
    }

    public void setLocations(List<Location> locations){
        this.locations = locations;
    }

    public List<Location> getLocations(){return locations;}

    public void setSelectedLocationNames(List<String> selectedLocationNames){
        this.selectedLocationNames = selectedLocationNames;
    }

    public List<String> getSelectedLocationNames(){return selectedLocationNames;}


    public static synchronized LocationsCache getInstance() {
        if (null == _sInstance){
            _sInstance = new LocationsCache();
        }
        return _sInstance;
    }

    public void clear(){
        if(_sInstance != null) {

            _sInstance.locations = new ArrayList<Location>();
        }
    }
}
