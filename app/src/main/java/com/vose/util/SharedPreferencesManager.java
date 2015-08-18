package com.vose.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vose.data.model.company.Company;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/10/17.
 */

//singletone pattern to manage SharedPreference
public class SharedPreferencesManager {
    private  static SharedPreferences _sharedPreferences;
    private  static SharedPreferencesManager  sharedPreferencesManager;


    public SharedPreferencesManager(SharedPreferences _sharedPreferences){
        this._sharedPreferences = _sharedPreferences;
    }

    public static synchronized  SharedPreferencesManager getInstance(Context context){
        if( sharedPreferencesManager == null){
            sharedPreferencesManager = new SharedPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context));
        }
        return sharedPreferencesManager;
    }


    public synchronized void putCompany(String key, Company value){
        SharedPreferences.Editor prefsEditor = _sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }


    public Company putCompany(String key){

        Gson gson = new Gson();
        String json = _sharedPreferences.getString(key, "");
        return gson.fromJson(json, Company.class);
    }





    public synchronized void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public synchronized void putInteger(String key, Integer value){
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public static synchronized void putString(String key, String value){
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public Boolean putBoolean(String key){
        return _sharedPreferences.getBoolean(key, false);
    }

    public Integer getInteger(String key, int defaultValue){
        return  _sharedPreferences.getInt(key, defaultValue);
    }

    public static String getString(String key, String defaultValue){
        return  _sharedPreferences.getString(key, defaultValue);
    }

    public synchronized void removeByKey(String key){
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }


    public static void putStringDateMap(String key, Map<String, Date> map){

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(map);
        putString(key, value);
    }

    public static Map<String, Date> getStringDateMap(String key){

        String value = getString(key, null);

        if(value == null){
            return null;
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Map<String, Date> map = gson.fromJson(value, new TypeToken<Map<String, Date>>(){}.getType());

        return map;
    }

    public static void putStringList(String key, List<String> stringList){

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(stringList);
        putString(key, value);
    }

    public static List<String> getStringList(String key){

        String value = getString(key, null);

        if(value == null){
            return null;
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        List<String> stringList = gson.fromJson(value, new TypeToken<List<String>>(){}.getType());

        return stringList;
    }

    public static void updateStringDateMap(String key, String id, Date newValue){

        Map<String, Date> map = getStringDateMap(key);
        map.put(id,newValue);
        putStringDateMap(key, map);
    }
}
