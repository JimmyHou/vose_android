package com.vose.fragment.maintabs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.AsyncTask.GetFollowingCompaniesByUserTask;
import com.vose.AsyncTask.GetPostsByCompanyBeforeCreatedTimeTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.adapter.FavoriteCompanyListAdapter;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.data.model.company.Company;
import com.vose.data.model.util.ComponentSource;
import com.vose.fragment.postcomment.CompanyPostsFragment;
import com.vose.fragment.postcomment.PostCommentsFragment;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/13.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FavoriteCompaniesFragment extends BaseFragment {

    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "FollowingCompaniesFrag";
    private static final String FRAGMENT_TAG = "FRAGMENT_FAVORITE_COMPANIES";

    //-----------
    // Member vars
    //-------------


    private FavoriteCompanyListAdapter mAdapter = null;
    private View fNoFollowingCompanyMessage = null;
    private Handler handler = null;


    public static FavoriteCompaniesFragment newInstance(){
        return new FavoriteCompaniesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Utility.trackParseEvent(AnalyticsConstants.FOLLOWING_TAB, null, null, null);

        View rootView = inflater.inflate(R.layout.fragment_favorite_companies, container, false);
        fNoFollowingCompanyMessage =  rootView.findViewById(R.id.no_following_company_message);

        SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.COMPANY_IS_CLICKED, false);

        handler = new Handler();

        return rootView;
    }



    private void updateListView(List<Company> followingCompanies){


        if(Utility.listIsEmpty(followingCompanies)) {
            fNoFollowingCompanyMessage.setVisibility(View.VISIBLE);
        }else{

            fNoFollowingCompanyMessage.setVisibility(View.GONE);
        }

        mAdapter = new FavoriteCompanyListAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, followingCompanies, fNoFollowingCompanyMessage);
        setListAdapter(mAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();
    }


    @Override
    public void onResume(){

        super.onResume();

        reloadFollowingCompanies();
    }


    @Override
    protected void handleLocalBroadcast(Context context, Intent intent)
    {
        String intentAction = intent.getAction();

        if(intentAction.equals(Constants.UPDATE_FOLLOWING_COMPANIES_VIEW))
        {
            reloadFollowingCompanies();
        }
    }


    private void reloadFollowingCompanies(){

        List<Company> followingCompanies = FollowingCompaniesCache.getInstance().getFollowingCompanies();

        if(Utility.listIsEmpty(followingCompanies)){

            try {
                followingCompanies = new GetFollowingCompaniesByUserTask().execute(ParseUser.getCurrentUser()).get();
            } catch (Exception e) {
                Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
            }
        }

        updateListView( followingCompanies);
     }


    // share the same code with hotCompanyListFragment
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.COMPANY_IS_CLICKED)) {
            // pass the company name to the next activity, cast to String temporarily, should actually pass in the Company entity!
            super.onListItemClick(l, v, position, id);
            SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.COMPANY_IS_CLICKED, true);

            final Company selectedCompany = (Company) l.getItemAtPosition(position);

            final GetPostsByCompanyBeforeCreatedTimeTask getPostsByCompanyBeforeCreatedTimeTask = new GetPostsByCompanyBeforeCreatedTimeTask(new Date());
            getPostsByCompanyBeforeCreatedTimeTask.execute(selectedCompany);

            ImageView hasNewPostMark = (ImageView) v.findViewById(R.id.has_new_post_mark);
            hasNewPostMark.setVisibility(View.GONE);


            //use a call back finish onCreateView method first and callback later on
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() { // This thread runs in the UI
                        @Override
                        public void run() {


                            CompanyPostsFragment companyPostsFragment = CompanyPostsFragment.newInstance(selectedCompany, ComponentSource.FavoriteCompaniesFragment, getPostsByCompanyBeforeCreatedTimeTask, null);

                            ((MainTabHostActivity)getActivity()).pushFragmentByNewThread(handler, companyPostsFragment, android.R.id.tabhost, 0, PostCommentsFragment.FRAGMENT_TAG);

                        }
                    });
                }
            };
            new Thread(runnable).start();
        }
    }

}
