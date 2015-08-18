package com.vose.fragment.launch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.voice.app.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.AsyncTask.GetFollowingCompaniesByUserTask;
import com.vose.AsyncTask.GetHotCompaniesByIndustryCodeTask;
import com.vose.AsyncTask.GetMostRecentlyLikedPostIdsTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.activity.SingupLoginActivity;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.cache.HotCompaniesCache;
import com.vose.cache.HotVoicesCache;
import com.vose.cache.LocationsCache;
import com.vose.data.model.util.ComponentSource;
import com.vose.util.Constants;
import com.vose.util.CustomDialog;
import com.vose.util.ParseDataBaseMapper;
import com.vose.util.ParseDatabaseColumnNames;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 1/3/15.
 */
public class LoginFragment extends Fragment {

    private static final String LOG_TAG = "LoginFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_LOGIN";


    private EditText fUsernameView;
    private EditText fPasswordView;
    private TextView fSignupLink;
    private Button fLoginButton;
    private String mIndustryCode;
    private List<String> mLocationNames;
    private Dialog fVerifyingAccountLoadingDialog;

    private String mUserName;
    private String mPassword;

    private SingupLoginActivity mainActivity;

    public static LoginFragment newInstance() {

        LoginFragment loginFragment = new LoginFragment();

        return loginFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View fRootView = inflater.inflate(R.layout.fragment_login, container, false);
        mainActivity = (SingupLoginActivity)getActivity();
        mainActivity.getActionBar().hide();

        //set up the sign up form
        fUsernameView = (EditText) fRootView.findViewById(R.id.username);
        fPasswordView = (EditText) fRootView.findViewById(R.id.password);
        fSignupLink = (TextView) fRootView.findViewById(R.id.signup_link);
        fLoginButton = (Button) fRootView.findViewById(R.id.login_button);



        mUserName = SharedPreferencesManager.getInstance(mainActivity).getString(Constants.USER_NAME, "");
        if(!Utility.isEmptyString(mUserName)) {
            fUsernameView.setText(mUserName, TextView.BufferType.EDITABLE);
        }



        mPassword = SharedPreferencesManager.getInstance(mainActivity).getString(Constants.PASS_WORD, "");
        if(!Utility.isEmptyString(mPassword)) {
            fPasswordView.setText(mPassword, TextView.BufferType.EDITABLE);
        }

        setViewItems();


        return fRootView;
    }


    private void setViewItems(){

        //switch to singup view
        fSignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignupFragment signupFragment = SignupFragment.newInstance();

                mainActivity.popCurrentFragment();

                mainActivity.pushFragment(signupFragment,  SignupFragment.FRAGMENT_TAG);

            }
        });

        //set up submit button click handler
        fLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate the login data
                boolean validationError = false;
                StringBuilder validationErrorMessage = new StringBuilder("Please ");
                if(isEmpty(fUsernameView)){
                    validationError = true;
                    validationErrorMessage.append(getString(R.string.enter_user_name));
                }
                if(isEmpty(fPasswordView)){
                    if(validationError)
                        validationErrorMessage.append(", and");
                    validationError = true;
                    validationErrorMessage.append(getString(R.string.enter_pass_word));
                }
                validationErrorMessage.append(".");

                // if there is a validation error, display the error message
                if(validationError) {

                    Utility.showShortToastMessage(mainActivity, validationErrorMessage.toString());

                    return;
                }

                loginParseAccount();

            }
        });




    }

    private void loginParseAccount(){

        fVerifyingAccountLoadingDialog = CustomDialog.getLoadingProgressDialog(getActivity(), getString(R.string.verify_account_message));

        //call the Parse login method, but don't dismiss the dialog here, we want to see the progressDialog while logging in
        mUserName = fUsernameView.getText().toString();
        mPassword = fPasswordView.getText().toString();

        ParseUser.logInInBackground(mUserName, mPassword, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {

                if (e != null) {
                    //show error message
                    fVerifyingAccountLoadingDialog.dismiss();
                    Utility.showShortToastMessage(mainActivity, e.getMessage());


                } else {

                    if(Utility.isUserBlocked()) {

                        CustomDialog.showOKReminderDialog(getActivity(), getResources().getString(R.string.user_block_message));
                        fVerifyingAccountLoadingDialog.dismiss();

                        return;

                    }


                    SharedPreferencesManager.getInstance(mainActivity).putString(Constants.USER_NAME, mUserName);
                    SharedPreferencesManager.getInstance(mainActivity).putString(Constants.PASS_WORD, mPassword);


                    ParseUser user = ParseUser.getCurrentUser();
                    if (!setAndCheckIndustry(user)) {
                        return;
                    }

                   user.put(ParseDatabaseColumnNames.USER_OS,"Android " + Build.VERSION.RELEASE);
                   user.put(ParseDatabaseColumnNames.USER_DEVICE, Build.MODEL);
                   user.saveInBackground();
                   preLoadData(user);

                   Intent mainIntent = new Intent(mainActivity, MainTabHostActivity.class);
                   startActivity(mainIntent);
                   mainActivity.hideKeyboard(fPasswordView);

                   mainActivity.finish();
                   fVerifyingAccountLoadingDialog.dismiss();

                }

            }
        });
    }


    private void preLoadData(ParseUser user){

        //preload companies by industry and locations and store the data into cache with in Task

        FollowingCompaniesCache.getInstance().clear();
        HotVoicesCache.getInstance().clear();
        HotCompaniesCache.getInstance().clear();

        new GetFollowingCompaniesByUserTask().execute(user);
        new GetMostRecentlyLikedPostIdsTask().execute(user);


        //use get since need to make sure hot companies is loaded before the next activity is started
        try {
            new GetHotCompaniesByIndustryCodeTask().execute(mIndustryCode).get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }
    }



    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }



    private boolean setAndCheckIndustry(ParseUser user){

        mIndustryCode = (String) user.get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);

        //for uses haven't finished onBoarding process
        if(mIndustryCode == null){
            OnboardingSelectIndustryFragment onboardingFragment = OnboardingSelectIndustryFragment.newInstance(ComponentSource.LoginFragment);
            mainActivity.pushFragment(onboardingFragment, OnboardingSelectIndustryFragment.FRAGMENT_TAG);

            if(fVerifyingAccountLoadingDialog!=null)
                fVerifyingAccountLoadingDialog.dismiss();

            mainActivity.hideKeyboard(fPasswordView);

            return  false;
        }

        //update cache
        SharedPreferencesManager.getInstance(mainActivity).putString(Constants.INTERESTED_INDUSTRY_CODE, mIndustryCode);

        return  true;

    }



    // for location version, out of use now
    private boolean setAndCheckIndustryLocation(ParseUser user){

        mIndustryCode = (String) user.get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);
        String locationNames = (String) user.get(ParseDatabaseColumnNames.USER_INTERESTED_LOCATION_NAMES);
        mLocationNames = ParseDataBaseMapper.parseStringToLocationNames(locationNames);

        //for uses haven't finished onBoarding process
        if(mIndustryCode == null || mLocationNames == null || mLocationNames.size() == 0){
            OnboardingSelectIndustryFragment onboardingFragment = new OnboardingSelectIndustryFragment().newInstance(ComponentSource.LoginFragment);
            mainActivity.pushFragment(onboardingFragment, onboardingFragment.FRAGMENT_TAG);

            if(fVerifyingAccountLoadingDialog!=null)
                fVerifyingAccountLoadingDialog.dismiss();

            mainActivity.hideKeyboard(fPasswordView);

            return  false;
        }

        //update cache
        SharedPreferencesManager.getInstance(mainActivity).putString(Constants.INTERESTED_INDUSTRY_CODE, mIndustryCode);
        LocationsCache.getInstance().setSelectedLocationNames(mLocationNames);

        return  true;

    }

}
