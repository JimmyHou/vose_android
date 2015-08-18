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
import android.widget.TextView;

import com.voice.app.R;
import com.vose.activity.MainTabHostActivity;
import com.vose.data.model.util.ComponentSource;

/**
 * Created by jimmyhou on 1/17/15.
 */
public class RuleContentsFragment extends Fragment{

    private static final String LOG_TAG = "RuleContentsFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_RULE_CONTENT";

    private View fRootView;
    private ComponentSource componentSource;
    private String ruleContents;
    private String ruleTitle;

    public static RuleContentsFragment newInstance(ComponentSource componentSource, String ruleTitle, String ruleContents) {

        RuleContentsFragment ruleContentsFragment = new RuleContentsFragment();

        ruleContentsFragment.componentSource = componentSource;
        ruleContentsFragment.ruleContents = ruleContents;
        ruleContentsFragment.ruleTitle = ruleTitle;

        return ruleContentsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        fRootView = inflater.inflate(R.layout.fragment_rule_contents, container, false);

        setActionBar();

        setTextView();

        return fRootView;
    }

    private void setTextView(){

        TextView content = (TextView) fRootView.findViewById(R.id.rule_content);
        content.setText(ruleContents);
    }


    private void setActionBar(){

        setHasOptionsMenu(true);

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(ruleTitle);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        if(componentSource == ComponentSource.MainTabHostActivity) {
            ((MainTabHostActivity) getActivity()).hideActionBarMenuItems(menu);

            MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
            overFlowMenu.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:

                getActivity().getSupportFragmentManager().popBackStack();

                if(componentSource == ComponentSource.MainTabHostActivity) {

                    MainTabHostActivity mainActivity = (MainTabHostActivity) getActivity();

                    //hide go back button
                    mainActivity.resetPreActivityActionBar();

                    mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);

                }
                return false;
        }

        return false;
    }
}
