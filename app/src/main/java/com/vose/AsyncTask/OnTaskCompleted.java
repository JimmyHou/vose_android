package com.vose.AsyncTask;

/**
 * Created by jimmyhou on 4/4/15.
 *
 * for asyntask completed callback
 */
public interface OnTaskCompleted<T> {

    void onTaskCompleted(T result);
}
