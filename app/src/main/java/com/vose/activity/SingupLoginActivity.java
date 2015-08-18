package com.vose.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.voice.app.R;
import com.vose.AsyncTask.GetAllCompaniesTask;
import com.vose.AsyncTask.GetIndustriesTask;
import com.vose.fragment.launch.LoginFragment;
import com.vose.fragment.launch.SignupFragment;
import com.vose.fragment.launch.WelcomeFragment;
import com.vose.util.Constants;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 1/3/15.
 */
public class SingupLoginActivity extends FragmentActivity {

    PageAdapter pageAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState){

        getActionBar().setDisplayHomeAsUpEnabled(false);

        super.onCreate(savedInstanceState);

        loadCompanyAndIndustryData();

        launchFragment();

    }


    private void launchFragment(){

       if(!SharedPreferencesManager.getInstance(this).putBoolean(Constants.HAS_LOGGED_IN)) {

           setContentView(R.layout.welcome_fragment_pager);

           List<Fragment> fragments = new ArrayList<Fragment>();
           fragments.add(WelcomeFragment.newInstance());
           fragments.add(SignupFragment.newInstance());

           pageAdapter = new PageAdapter(getSupportFragmentManager(), fragments);


           ViewPager pager = (ViewPager) findViewById(R.id.viewpager);


           pager.setAdapter(pageAdapter);

        }
        else {

            LoginFragment loginFragment = LoginFragment.newInstance();

            pushFragment(loginFragment, LoginFragment.FRAGMENT_TAG);
        }
    }

    //restart is being called after the new activity is started
    @Override
    protected void onRestart(){
        super.onRestart();

        Utility.resetParameters();
    }


    @Override
    public void onBackPressed()
    {
        return;

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void pushFragment(Fragment fragment, String fragmentTag){
        //since it's called at acitivty, can't just call fm.popBackStack();
        //setContentView(R.layout.fragment_onboarding);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(android.R.id.content, fragment, fragmentTag).addToBackStack(fragmentTag);
        transaction.commit();

    }

    private void loadCompanyAndIndustryData(){

        new GetAllCompaniesTask(this).execute();

        new GetIndustriesTask(this).execute("");
    }

    public void hideKeyboard(EditText editText){
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public void popCurrentFragment(){

        //hide any keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }


    class PageAdapter extends FragmentPagerAdapter {


        private List<Fragment> fragments;

        public PageAdapter(FragmentManager fm, List<Fragment> fragments) {

            super(fm);
            this.fragments = fragments;
        }



        @Override

        public Fragment getItem(int position) {

            return this.fragments.get(position);

        }

        @Override

        public int getCount() {

            return this.fragments.size();

        }

    }




}
