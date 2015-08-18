package com.vose.fragment.launch;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.AsyncTask.GetHotCompaniesByIndustryCodeTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.cache.IndustriesCache;
import com.vose.cache.LocationsCache;
import com.vose.core.data.dao.user.UserDAOImpl;
import com.vose.core.data.service.user.UserService;
import com.vose.core.data.service.user.UserServiceImpl;
import com.vose.customizedUI.MultiSelectionSpinner;
import com.vose.data.model.company.Location;
import com.vose.data.model.company.ParseIndustry;
import com.vose.data.model.util.ComponentSource;
import com.vose.util.Constants;
import com.vose.util.CustomDialog;
import com.vose.util.ParseDataBaseMapper;
import com.vose.util.ParseDatabaseColumnNames;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jimmyhou on 2014/11/25.
 */
public class OnboardingWithLocationFragment extends Fragment {
    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "OnboardingFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_ONBOARDING";

    private Spinner fIndustrySpinner;
    private MultiSelectionSpinner fLocationSpinner;
    private View fOnboardingLayout;


    private String mSelectedIndustryCode;
    private ComponentSource componentSource;
    private List<ParseIndustry> mIndustries;
    private List<Location> mLocations;
    private List<String> mIndustryNameList ;
    private List<String> mLocationNameList;




    public OnboardingWithLocationFragment newInstance(ComponentSource componentSource){

        OnboardingWithLocationFragment onboardingSelectIndustryFragment = new OnboardingWithLocationFragment();
        onboardingSelectIndustryFragment.componentSource = componentSource;

        return onboardingSelectIndustryFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){


        fOnboardingLayout = inflater.inflate(R.layout.fragment_onboarding_with_location, container, false);
        mIndustryNameList = new ArrayList<String>();
        mLocationNameList = new ArrayList<String>();

        //set actionBar
        if(componentSource == ComponentSource.MainTabHostActivity) {
            setHasOptionsMenu(true);
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().getActionBar().setTitle(getString(R.string.change_industry_title));
        }else{
            getActivity().getActionBar().setTitle(getString(R.string.set_industry_title));
        }

        fOnboardingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("S");
            }
        });

        setIndustrySpinner();
        setLocationSpinner();
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
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
                resetPreActivityActionBar();


                return false;
        }

        return false;
    }

    private void resetPreActivityActionBar(){
        if(componentSource == ComponentSource.MainTabHostActivity){
            ((MainTabHostActivity) getActivity()).resetActionBar();
        }else {
            getActivity().setTitle(getString(R.string.slogan));
        }
    }


    private void setIndustrySpinner(){

        fIndustrySpinner = (Spinner)fOnboardingLayout.findViewById(R.id.industry_spinner);

        populateIndustry();

        ArrayAdapter<String> industryDataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mIndustryNameList);
        industryDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fIndustrySpinner.setAdapter(industryDataAdapter);

        setIndustrySpinnerFromUser();


        fIndustrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSelectedIndustryCode = mapIndustryCodeFromName(parent.getItemAtPosition(position).toString());

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

        int spinnerPosition = industryAdapter.getPosition(mapIndustryNameFromCode(userIndustryCode));

        fIndustrySpinner.setSelection(spinnerPosition);
    }


    private void setLocationSpinner(){
        fLocationSpinner = (MultiSelectionSpinner)fOnboardingLayout.findViewById(R.id.location_spinner);
        populateLocation();
        //casting fails to convert Object[] to String[]
        fLocationSpinner.setItems(mLocationNameList.toArray(new String[mLocationNameList.size()]));

        setLocationSpinnerSelectionFromUser();
    }

    private void setLocationSpinnerSelectionFromUser(){
        List<String> userLocationNames = ParseDataBaseMapper.parseStringToLocationNames((String) ParseUser.getCurrentUser().get(ParseDatabaseColumnNames.USER_INTERESTED_LOCATION_NAMES));

        //default as the first one if it's empty
        if(Utility.listIsEmpty(userLocationNames)) {
            if(userLocationNames == null){
                userLocationNames = new ArrayList<String>();
            }

            userLocationNames.add(mLocationNameList.get(0));
        }

        fLocationSpinner.setSelection(userLocationNames);
    }




    public void onClick(View v){
        String s = fLocationSpinner.getSelectedItemsAsString();
        Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private void setStartButton(){
        Button nextButton = (Button) fOnboardingLayout.findViewById(R.id.button_next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.getLoadingProgressDialog(getActivity(), getResources().getString(R.string.loading_message)).show();

                if (Utility.listIsEmpty(fLocationSpinner.getSelectedStrings())) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.empty_location), Toast.LENGTH_SHORT).show();
                    return;
                }


                LocationsCache.getInstance().setSelectedLocationNames(fLocationSpinner.getSelectedStrings());
                updateUserIndustryLocationsInDatabase(mSelectedIndustryCode, fLocationSpinner.getSelectedStrings());

                startCompanyTabHostActivity();
            }
        });

    }


    private void updateUserIndustryLocationsInDatabase(String selectedIndustryCode, List<String> locationNames){
        SharedPreferencesManager.getInstance(getActivity()).putString(Constants.INTERESTED_INDUSTRY_CODE, mSelectedIndustryCode);
        //update it in user db, locations is not set yet
        UserService userService = new UserServiceImpl(new UserDAOImpl());
        userService.updateUserIndustryAndLocationNames(selectedIndustryCode, locationNames);

    }

    private void populateIndustry(){

        mIndustries = IndustriesCache.getInstance().getIndustries();

        for(ParseIndustry industry: mIndustries){
            mIndustryNameList.add(industry.getName());
        }

    }

    private void populateLocation(){

        mLocations = LocationsCache.getInstance().getLocations();

        for(Location location: mLocations){
            mLocationNameList.add(location.getName());
        }

        //sort alphabetically
        Collections.sort(mLocationNameList);
    }


    private String mapIndustryCodeFromName(String selectedIndustryName){
        for (ParseIndustry industry: mIndustries){
            if(industry.getName().equals(selectedIndustryName))
                return industry.getIndustryCode();
        }

        return  null;
    }

    private String mapIndustryNameFromCode(String selectedIndustryCode){
        for (ParseIndustry industry: mIndustries){
            if(industry.getIndustryCode().equals(selectedIndustryCode))
                return industry.getName();
        }
        return  null;
    }


    private void startCompanyTabHostActivity(){

        String industryCode = SharedPreferencesManager.getInstance(getActivity()).getString(Constants.INTERESTED_INDUSTRY_CODE, "software");

        try {
            new GetHotCompaniesByIndustryCodeTask().execute(industryCode).get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }


        Intent companyTabHostActivity = new Intent(getActivity(), MainTabHostActivity.class);
        startActivity(companyTabHostActivity);

        if(componentSource == ComponentSource.MainTabHostActivity)
            getActivity().finish();
    }


}
