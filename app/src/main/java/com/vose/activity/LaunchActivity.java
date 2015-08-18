package com.vose.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by jimmyhou on 1/4/15.
 */
public class LaunchActivity extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        getActionBar().hide();
        //hide the go back action bar
        getActionBar().setDisplayHomeAsUpEnabled(false);

        // for CompanyTabHostAcvity log out issue, we need to have a lunchAcivity as launcher
        // can't directly call SingupLoginActivty, otherwise when clicking
        //home button and come back, SingupLoginActivty will be called again


        Intent intent = new Intent(LaunchActivity.this, SingupLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

}
