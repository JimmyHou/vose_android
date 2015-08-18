package com.vose.fragment.launch;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.AsyncTask.GetHotCompaniesByIndustryCodeTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.activity.SingupLoginActivity;
import com.vose.adapter.IndustrySpinnerAdapter;
import com.vose.core.data.dao.user.UserDAOImpl;
import com.vose.core.data.service.user.UserService;
import com.vose.core.data.service.user.UserServiceImpl;
import com.vose.data.model.company.Industry;
import com.vose.data.model.util.ComponentSource;
import com.vose.util.Constants;
import com.vose.util.ParseDatabaseColumnNames;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmyhou on 12/20/14.
 */
public class OnboardingSelectIndustryFragment extends Fragment {
    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "OnBoardingIndustryFrg";
    public static final String FRAGMENT_TAG = "FRAGMENT_ONBOARDING_NO_LOCATION";

    private Spinner fIndustrySpinner;
    private View fOnboardingLayout;
    private String mSelectedIndustryCode;
    private ComponentSource componentSource;
    private List<String> mIndustryNameList ;
    private Handler handler;




    public static OnboardingSelectIndustryFragment newInstance(ComponentSource componentSource){

        OnboardingSelectIndustryFragment onboardingFragment = new OnboardingSelectIndustryFragment();
        onboardingFragment.componentSource = componentSource;

        return  onboardingFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.HAS_LOGGED_IN, true);
        handler = new Handler();;

        fOnboardingLayout = inflater.inflate(R.layout.fragment_onboarding_select_industry, container, false);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        mIndustryNameList = new ArrayList<String>();

        //set actionBar
        if(componentSource == ComponentSource.MainTabHostActivity) {
            setHasOptionsMenu(true);
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().getActionBar().setTitle(getString(R.string.change_industry_title));
        }else{
            getActivity().getActionBar().setTitle(getString(R.string.set_industry_title));
        }

        setIndustrySpinner();
        setStartButton();

        return fOnboardingLayout;
    }


    //hide up action bar items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        if(componentSource == ComponentSource.MainTabHostActivity) {
            MenuItem postActionFromTabsActivity = menu.findItem(R.id.action_make_post_tabs_activity);
            postActionFromTabsActivity.setVisible(false);

            MenuItem postActionFromCompanyPostsFragment = menu.findItem(R.id.action_make_post_company_posts_fragment);
            postActionFromCompanyPostsFragment.setVisible(false);

            MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
            overFlowMenu.setVisible(false);

        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    //"Return false to allow normal menu processing to proceed, true to consume it here."
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MainTabHostActivity mainActivity = (MainTabHostActivity) getActivity();

        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();

                //hide go back button
                mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);

                resetPreActivityActionBar();

                return false;
        }

        return false;
    }

    private void resetPreActivityActionBar(){
        if(componentSource == ComponentSource.MainTabHostActivity){

            ((MainTabHostActivity) getActivity()).resetActionBar();
            ((MainTabHostActivity) getActivity()).showIndustryNavigatorWithoutLoading();

        }else {
            getActivity().setTitle(getString(R.string.slogan));
        }
    }


    private void setIndustrySpinner(){

        fIndustrySpinner = (Spinner)fOnboardingLayout.findViewById(R.id.industry_spinner);

        mIndustryNameList = Utility.populateIndustryNameWithoutExtra();

        //ArrayAdapter<String> industryDataAdapter = new ArrayAdapter(getActivity(),R.layout.onboarding_indutry_spinner, mIndustryNameList);
        IndustrySpinnerAdapter industryDataAdapter = new IndustrySpinnerAdapter(getActivity(), mIndustryNameList);

        //industryDataAdapter.setDropDownViewResource(R.layout.onboarding_industry_spinner_dropdown);

        fIndustrySpinner.setAdapter(industryDataAdapter);



        setIndustrySpinnerFromUser();


        fIndustrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSelectedIndustryCode = Utility.mapIndustryNameToCode(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

    }

    private void setIndustrySpinnerFromUser(){
        String userIndustryCode = (String) ParseUser.getCurrentUser().get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);
        if(Utility.stringIsEmpty(userIndustryCode))
            return;

        ArrayAdapter industryAdapter = (ArrayAdapter) fIndustrySpinner.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = industryAdapter.getPosition(Utility.mapIndustryCodeToName(userIndustryCode));

        fIndustrySpinner.setSelection(spinnerPosition);
    }



    private void setStartButton(){
        Button startButton = (Button) fOnboardingLayout.findViewById(R.id.button_next);

        if(componentSource == ComponentSource.MainTabHostActivity) {
            startButton.setText(R.string.restart_voice);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUserIndustryInDatabase(mSelectedIndustryCode);
                startOnboardingFollowCompaniesFragment();

            }
        });

    }


    public void updateUserIndustryInDatabase(String selectedIndustryCode){

        SharedPreferencesManager.getInstance(getActivity()).putString(Constants.INTERESTED_INDUSTRY_CODE, selectedIndustryCode);
        UserService userService = new UserServiceImpl(new UserDAOImpl());
        userService.updateUserIndustryCode(selectedIndustryCode);

    }

    public void startOnboardingFollowCompaniesFragment(){

        String industryCode = SharedPreferencesManager.getInstance(getActivity()).getString(Constants.INTERESTED_INDUSTRY_CODE, Industry.TECHNOLOGY.getCode());

        try {
            new GetHotCompaniesByIndustryCodeTask().execute(industryCode);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }


        SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.FIRST_TIME_SIGN_UP, true);

        GetHotCompaniesByIndustryCodeTask getHotCompaniesByIndustryCodeTask = new GetHotCompaniesByIndustryCodeTask();
        getHotCompaniesByIndustryCodeTask.execute(industryCode);

        OnboardingFollowCompaniesFragment followCompaniesFragment = OnboardingFollowCompaniesFragment.newInstance(getHotCompaniesByIndustryCodeTask );
        ((SingupLoginActivity)getActivity()).pushFragment(followCompaniesFragment, followCompaniesFragment.FRAGMENT_TAG);

    }
}
