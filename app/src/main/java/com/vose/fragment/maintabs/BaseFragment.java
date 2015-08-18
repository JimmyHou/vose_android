package com.vose.fragment.maintabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vose.util.Constants;

/**
 * Created by jimmyhou on 12/28/14.
 */
public abstract class BaseFragment extends ListFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        return super.onCreateView(inflater,container, savedInstanceState);
    }

    @Override
    public  void onStart(){
        super.onStart();
        registerBroadcastReceivers();
    }


    @Override
    public void onStop()
    {

        super.onStop();

        unregisterBroadcastReceivers();
    }


    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            handleLocalBroadcast(context, intent);
        }
    };

    protected abstract void handleLocalBroadcast(Context context, Intent intent);


    protected void registerBroadcastReceivers()
    {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        lbm.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.UPDATE_HOT_VOICES_VIEW));
        lbm.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.UPDATE_HOT_COMPANIES_VIEW));
        lbm.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.UPDATE_FOLLOWING_COMPANIES_VIEW));
    }

    protected void unregisterBroadcastReceivers()
    {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        lbm.unregisterReceiver(mBroadcastReceiver);
    }
}
