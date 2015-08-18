package com.vose.fragment.launch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.voice.app.R;
import com.vose.activity.SingupLoginActivity;

/**
 * Created by jimmyhou on 1/4/15.
 */
public class WelcomeFragment extends Fragment {

    private static final String LOG_TAG = "WelcomeFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_WELCOME";
    private Button continueButton;



    public static WelcomeFragment newInstance() {

        WelcomeFragment welcomeFragment = new WelcomeFragment();

        return welcomeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View fRootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        continueButton = (Button) fRootView.findViewById(R.id.continue_button);

        setContinueButton();

        getActivity().getActionBar().hide();

        return fRootView;
    }


    private void setContinueButton(){

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SingupLoginActivity)getActivity()).pushFragment(SignupFragment.newInstance(), SignupFragment.FRAGMENT_TAG);
            }
        });

    }
}
