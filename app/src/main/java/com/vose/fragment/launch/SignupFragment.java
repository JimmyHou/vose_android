package com.vose.fragment.launch;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.voice.app.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.vose.activity.SingupLoginActivity;
import com.vose.cache.AlreadyLikedPostIdsCache;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.data.model.util.ComponentSource;
import com.vose.data.model.util.UserStatus;
import com.vose.fragment.action.RulesFragment;
import com.vose.util.Constants;
import com.vose.util.CustomDialog;
import com.vose.util.ParseDatabaseColumnNames;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

/**
 * Created by jimmyhou on 1/3/15.
 */
public class SignupFragment extends Fragment {


    private static final String LOG_TAG = "SignUpFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_SIGNUP";


    //UI references
    private EditText fUsernameView;
    private EditText fPasswordView;
    private EditText fPasswordAgainView;
    private TextView fLoginLink;
    private Button fSignupButton;
    private Dialog fSingupProgressDialog;
    private TextView rulesView;

    private SingupLoginActivity mainActivity;

    public static SignupFragment newInstance() {

        SignupFragment signupFragment = new SignupFragment();

        return signupFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mainActivity = (SingupLoginActivity) getActivity();

        mainActivity.getActionBar().hide();

        View fRootView = inflater.inflate(R.layout.fragment_signup, container, false);
        //set up the sign up form
        fUsernameView = (EditText) fRootView.findViewById(R.id.username);
        fPasswordView = (EditText) fRootView.findViewById(R.id.password);
        fPasswordAgainView = (EditText) fRootView.findViewById(R.id.passwordAgain);
        fLoginLink = (TextView) fRootView.findViewById(R.id.login_link);
        fSignupButton = (Button) fRootView.findViewById(R.id.login_button);
        rulesView = (TextView) fRootView.findViewById(R.id.rules);

        setViewItems();
        setActionBar();

        return fRootView;
    }


    private void setActionBar(){

        //most to have otherwise action fragment action bar title color is black...
        //The ActionBar title ID is hidden, or in other words, it's internal and accessing it can't be done typically.
        int actionBarTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView abTitle = (TextView) getActivity().findViewById(actionBarTitleId);
        abTitle.setTextColor(getActivity().getResources().getColor(R.color.white));

    }

    private void setViewItems(){

        //switch to log in page
        fLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start loginFragment
                LoginFragment loginFragment = LoginFragment.newInstance();

                mainActivity.popCurrentFragment();

                mainActivity.pushFragment(loginFragment, LoginFragment.FRAGMENT_TAG);

            }
        });

        //set up the submit button click handler
        fSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate the sign up data
                boolean validationError = false;
                StringBuilder validationErrorMessage =
                        new StringBuilder(getResources().getString(R.string.error_intro));
                if (isEmpty(fUsernameView)) {
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
                }
                if (isEmpty(fPasswordView)) {
                    if (validationError) {
                        validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
                }
                if(!isMatching(fPasswordView, fPasswordAgainView)){
                    if(validationError) {
                        validationErrorMessage.append(", and ");
                    }
                    validationError = true;
                    validationErrorMessage.append(getString(R.string.same_password_message));
                }
                validationErrorMessage.append(".");

                // If there is a validation error, display the error
                if (validationError) {

                    Utility.showShortToastMessage(getActivity(),validationErrorMessage.toString());

                    return;
                }

                signupParseAccount();
            }
        });

        rulesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RulesFragment rulesFragment = RulesFragment.newInstance(ComponentSource.SignupFragment);
                pushFragment(rulesFragment, RulesFragment.FRAGMENT_TAG);
                getActivity().getActionBar().show();
                getActivity().getActionBar().setTitle(getString(R.string.rules_title));
            }
        });

    }

    private void signupParseAccount(){

        fSingupProgressDialog = CustomDialog.getLoadingProgressDialog(getActivity(), getString(R.string.create_account_message));

        //Set up a new parser user, should use a service/DAO call
        ParseUser user = new ParseUser();
        user.setUsername(fUsernameView.getText().toString());
        user.setPassword(fPasswordView.getText().toString());
        user.put(ParseDatabaseColumnNames.USER_STATUS, UserStatus.ACTIVE.getCode());
        user.put(ParseDatabaseColumnNames.USER_OS,"Android " + Build.VERSION.RELEASE);
        user.put(ParseDatabaseColumnNames.USER_DEVICE, Build.MODEL);

        //call the Parse login method, but don't dismiss the dialog here, we want to see the progressDialog while logging in
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    //show the error message
                    fSingupProgressDialog.dismiss();
                    Utility.showShortToastMessage(getActivity(),e.getMessage());
                }
                else{

                    SharedPreferencesManager.getInstance(mainActivity).putString(Constants.USER_NAME, fUsernameView.getText().toString());
                    SharedPreferencesManager.getInstance(mainActivity).putString(Constants.PASS_WORD, fPasswordView.getText().toString());

                    FollowingCompaniesCache.getInstance().clear();
                    AlreadyLikedPostIdsCache.getInstance().clear();
                    OnboardingSelectIndustryFragment onboardingFragment = new OnboardingSelectIndustryFragment().newInstance(ComponentSource.SignupFragment);
                    ((SingupLoginActivity)getActivity()).pushFragment(onboardingFragment, onboardingFragment.FRAGMENT_TAG);

                    if(fSingupProgressDialog!=null)
                        fSingupProgressDialog.dismiss();

                    hideKeyboard(fPasswordView);

                }
            }
        });

    }



    private void pushFragment(Fragment fragment, String fragmentTag){
        //since it's called at acitivty, can't just call fm.popBackStack();
        //setContentView(R.layout.fragment_onboarding);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(android.R.id.content, fragment, fragmentTag).addToBackStack(null);
        transaction.commit();
        if(fSingupProgressDialog!=null)
            fSingupProgressDialog.dismiss();
    }


    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMatching(EditText etText1, EditText etText2){
        if(etText1.getText().toString().equals(etText2.getText().toString()))
            return true;
        else
            return false;
    }

    public void hideKeyboard(EditText editText){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
