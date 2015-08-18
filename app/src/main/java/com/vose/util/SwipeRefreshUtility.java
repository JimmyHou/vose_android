package com.vose.util;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by jimmyhou on 2014/11/23.
 */
public class SwipeRefreshUtility {

    public  static void setup(SwipeRefreshLayout refreshLayout, SwipeRefreshLayout.OnRefreshListener listener){
        refreshLayout.setOnRefreshListener(listener);

        setLoadingColors(refreshLayout);

    }

    private static void setLoadingColors(SwipeRefreshLayout refreshLayout){

        refreshLayout.setColorScheme(
                                     android.R.color.holo_blue_bright,
                                     android.R.color.holo_orange_light,
                                     android.R.color.holo_green_light,
                                     android.R.color.holo_red_light);
    }
}
