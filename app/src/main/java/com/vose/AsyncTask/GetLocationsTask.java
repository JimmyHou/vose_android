package com.vose.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.vose.core.data.dao.company.LocationDAOImpl;
import com.vose.core.data.service.company.LocationService;
import com.vose.core.data.service.company.LocationServiceImpl;
import com.vose.data.model.company.Location;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/30.
 */

public class GetLocationsTask extends AsyncTask<String, Integer,List<Location>> {
    private LocationService locationService;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        locationService = new LocationServiceImpl(new LocationDAOImpl());
    }

    @Override
    protected List<Location> doInBackground(String... params) {
        try {
            return locationService.getLocations();
        } catch (ParseException e) {
            Log.d("ERROR:", "failed to get locations from DB", e);
        }

        return null;
    }
}