package com.vose.helper;

import com.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;

/**
 * Created by jimmyhou on 2014/9/8.
 */
public class PrettyTimeHelper {

    public static String convertToSimplePrettyTime(Date time){
        PrettyTime prettyTimer = new PrettyTime();
        String prettyTime = prettyTimer.format(time);

        prettyTime = prettyTime.replace("moments ago", "now");

        prettyTime = prettyTime.replace("moments from now", "now");

        prettyTime = prettyTime.replace("minutes", "m");
        prettyTime = prettyTime.replace("ms", "m");

        prettyTime = prettyTime.replace("hour", "hr");
        prettyTime = prettyTime.replace("hrs", "hr");

        //for one week or weeks
        prettyTime = prettyTime.replace("week", "w");
        prettyTime = prettyTime.replace("ws", "w");

        prettyTime = prettyTime.replace("day", "d");
        prettyTime = prettyTime.replace("ds", "d");

        prettyTime = prettyTime.replace("month", "mo");
        prettyTime = prettyTime.replace("mos", "mo");

        prettyTime = prettyTime.replace("year", "yr");
        prettyTime = prettyTime.replace("yrs", "yr");

        return prettyTime.replaceFirst(" ", "").replace("ago","");
    }
}
