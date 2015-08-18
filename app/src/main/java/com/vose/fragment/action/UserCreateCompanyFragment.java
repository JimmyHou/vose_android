package com.vose.fragment.action;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.activity.MainTabHostActivity;
import com.vose.core.data.dao.company.UserCreatedCompanyDAOImpl;
import com.vose.core.data.service.company.UserCreatedCompanyService;
import com.vose.core.data.service.company.UserCreatedCompanyServiceImpl;
import com.vose.data.model.util.ComponentSource;
import com.vose.data.model.util.ExtraIndustry;
import com.vose.util.AnalyticsConstants;
import com.vose.util.CustomDialog;
import com.vose.util.ParseDatabaseColumnNames;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 12/24/14.
 */
public class UserCreateCompanyFragment extends Fragment {

    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "CreateCompanyFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_USER_CREATE_COMPANY_FRAGMENT";

    private View fCreateCompanyLayout;
    private EditText fCompanyNameView;
    private EditText fCompanyLocationView;
    private Spinner fIndustrySpinner;
    private String mSelectedIndustryCode;
    private ComponentSource componentSource;
    private List<String> mIndustryNameList ;
    private MainTabHostActivity mainActivity;

    public static UserCreateCompanyFragment newInstance(ComponentSource componentSource){

        UserCreateCompanyFragment createCompanyFragment = new UserCreateCompanyFragment();

        createCompanyFragment.componentSource = componentSource;

        return  createCompanyFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        Utility.trackParseEvent(AnalyticsConstants.SUGGEST_COMPANY_View, null, null, componentSource.getParseCode());

        fCreateCompanyLayout = inflater.inflate(R.layout.fragment_user_create_company, container, false);

        fCompanyNameView = (EditText) fCreateCompanyLayout .findViewById(R.id.created_company_name);

        fCompanyLocationView = (EditText) fCreateCompanyLayout.findViewById(R.id.created_company_location);

        ((MainTabHostActivity)getActivity()).showKeyBoard(fCompanyNameView);


        mainActivity = (MainTabHostActivity) getActivity();


        setActionBar();
        setIndustrySpinner();
        setStartButton();


        return fCreateCompanyLayout;
    }


    //hide up action bar items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){


            MenuItem postActionFromTabsActivity = menu.findItem(R.id.action_make_post_tabs_activity);
            postActionFromTabsActivity.setVisible(false);

            MenuItem postActionFromCompanyPostsFragment = menu.findItem(R.id.action_make_post_company_posts_fragment);
            postActionFromCompanyPostsFragment.setVisible(false);

            MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
            overFlowMenu.setVisible(false);


        super.onCreateOptionsMenu(menu, inflater);
    }

    //"Return false to allow normal menu processing to proceed, true to consume it here."
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                popBackFragment();

                return false;

        }

        return false;
    }

    private void popBackFragment(){
        getActivity().getSupportFragmentManager().popBackStack();

        //hide go back button
        mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);

        mainActivity.resetPreActivityActionBar();

        mainActivity.hideKeyboard(fCompanyNameView);

    }


    private void setActionBar(){

        setHasOptionsMenu(true);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(Utility.makeActionBarTitle(getString(R.string.user_create_company_title)));

    }




    private void setIndustrySpinner(){

        fIndustrySpinner = (Spinner)fCreateCompanyLayout.findViewById(R.id.industry_spinner);

        mIndustryNameList = Utility.populateIndustryNameWithoutExtra();

        //add other
        mIndustryNameList.add(mIndustryNameList.size(), ExtraIndustry.OTHER.getName());


        ArrayAdapter<String> industryDataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.action_bar_industry_spinner_item, mIndustryNameList);
        industryDataAdapter.setDropDownViewResource(R.layout.user_create_company_industry_spinner_dropdown);
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

        int spinnerPosition;

        String userIndustryCode = (String) ParseUser.getCurrentUser().get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);

        if(Utility.stringIsEmpty(userIndustryCode))
            return;

        if(Utility.isExtraIndustryCode(userIndustryCode)){

            spinnerPosition = 0;

        }else{
            ArrayAdapter industryAdapter = (ArrayAdapter) fIndustrySpinner.getAdapter(); //cast to an ArrayAdapter

            spinnerPosition = industryAdapter.getPosition(Utility.mapIndustryCodeToName(userIndustryCode));

        }


        fIndustrySpinner.setSelection(spinnerPosition);
    }



    private void setStartButton(){
        final Button createCompanyButton = (Button) fCreateCompanyLayout.findViewById(R.id.button_next);


        createCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String companyName = fCompanyNameView.getText().toString();
                String locationName = fCompanyLocationView.getText().toString();


                //prevent empty typing
                if(companyName.equals("")) {
                    Utility.showShortToastMessage(getActivity(), getResources().getString(R.string.empty_company_message));
                    return ;
                }
                // allow to create existing company

//                if(!Utility.listIsEmpty(Utility.SearchCompanyInCache(companyName))){
//
//                    Utility.showShortToastMessage(getActivity(), getResources().getString(R.string.company_existing_message));
//
//                    return;
//                }


                ((MainTabHostActivity)getActivity()).hideKeyboard(fCompanyNameView);


                CustomDialog.showOKReminderDialog(getActivity(), getResources().getString(R.string.finish_suggest_company_message));

                //hide button to look better
                createCompanyButton.setVisibility(View.GONE);

                saveCreatedCompanyInDatabase(companyName, locationName, mSelectedIndustryCode);

                popBackFragment();

            }
        });

    }


    public void saveCreatedCompanyInDatabase(String companyName,  String location, String selectedIndustryCode){

        UserCreatedCompanyService userCreatedCompanyService = new UserCreatedCompanyServiceImpl(new UserCreatedCompanyDAOImpl());
        userCreatedCompanyService.createCompany(companyName, location, selectedIndustryCode);

    }
}
