package com.vose.fragment.maintabs;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.AsyncTask.GetFollowingCompaniesByUserTask;
import com.vose.AsyncTask.GetHotCompaniesByIndustryCodeTask;
import com.vose.AsyncTask.GetPostsByCompanyBeforeCreatedTimeTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.adapter.CompanyListAdapter;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.cache.HotCompaniesCache;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.Industry;
import com.vose.data.model.util.ComponentSource;
import com.vose.data.model.util.ExtraIndustry;
import com.vose.fragment.action.UserCreateCompanyFragment;
import com.vose.fragment.makepost.MakePostFragment;
import com.vose.fragment.postcomment.CompanyPostsFragment;
import com.vose.fragment.postcomment.PostCommentsFragment;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.CustomDialog;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/8/12.
 */

public class HotCompaniesFragment extends BaseFragment {
    //----------
    // Enum and constants
    //-----------
    public static final String LOG_TAG = " HotCompaniesFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_HOT_COMPANIES";

    //-----------
    // Member vars
    //-------------

    private CompanyListAdapter mAdapter = null;
    private boolean showFollowIcon = true;
    private Handler handler = null;
    //should be propogated from onboarding page
    private String mIndustryCode  = null;

    private View rootView;
    private View noFollowingCompanyMessage;
    private View fSearchView;
    private View hotCompaniesSectionView;
    private TextView hotCompaniesSectionText;
    private View fNoSearchResultLayout;
    private ListView fCompanyListView;
    private EditText fSearchQueryEditText;
    private String mSearchQuery;
    private ImageView fCleanupSearchButton;
    private Button fCreateCompanyButton;

    private SearchPurpose searchPurpose;
    private MainTabHostActivity mainActivity;

    private final String STATE = "state";
    private final String SEARCH_QUERY = "search_query";
    private final String SEARCH_PURPOSE = "search_purpose";

//    private final String STATE = "state";
//    private final String  INDUSTRIES = "industries";


    public static HotCompaniesFragment newInstance(SearchPurpose searchPurpose){

        HotCompaniesFragment hotCompaniesFragment = new HotCompaniesFragment();

        hotCompaniesFragment.searchPurpose = searchPurpose;

        return  hotCompaniesFragment;
    }

    //recreate the state
    @Override
    public void onCreate(Bundle savedInstanceState){

        if(savedInstanceState!=null){

            Bundle state = (Bundle) savedInstanceState.get(STATE);

            searchPurpose = (SearchPurpose) state.get(SEARCH_PURPOSE);

            mSearchQuery = state.getString(SEARCH_QUERY);

        }


        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.COMPANY_IS_CLICKED, false);
        mainActivity = (MainTabHostActivity) getActivity();

        if(searchPurpose == null){

            searchPurpose = SearchPurpose.LOOK;

            Utility.trackParseEvent(AnalyticsConstants.COMPANIES_TAB, null, null, null);

        }else if(searchPurpose == SearchPurpose.POST){

            Utility.trackParseEvent(AnalyticsConstants.SEARCH_TO_POST, null, null, null);

        }

        if(searchPurpose == SearchPurpose.POST &&  SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.FIRST_TIME_SIGN_UP))
        {
            CustomDialog.showOKReminderDialog(getActivity(), getString(R.string.search_to_post_reminder));
            SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.FIRST_TIME_SIGN_UP, false);
        }

        handler = new Handler();


        mIndustryCode = SharedPreferencesManager.getInstance(getActivity()).getString(Constants.INTERESTED_INDUSTRY_CODE, Industry.TECHNOLOGY.getCode());

        getActivity().getActionBar().setTitle(getString(R.string.home));

        rootView = inflater.inflate(R.layout.fragment_hot_companies, container, false);


        fNoSearchResultLayout = rootView.findViewById(R.id.no_search_result_view);
        noFollowingCompanyMessage =  rootView.findViewById(R.id.no_following_company_message);



        if(searchPurpose == SearchPurpose.POST){
            setAsSearchToPostSection();
            rootView.setBackground(getResources().getDrawable(R.drawable.background_color));
        }

        setCreateCompanyButton();

        setSearchView();


        //if it's at following filter but no following companies cases
        if(mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode())) {

            if (Utility.listIsEmpty(FollowingCompaniesCache.getInstance().getFollowingCompanies())) {

                noFollowingCompanyMessage.setVisibility(View.VISIBLE);

                return  rootView;
            }else {
                noFollowingCompanyMessage.setVisibility(View.GONE);
            }
        }


        reloadSearchViewAndData();

        return rootView;
    }

    private boolean isFollowingIndustryAndNoFollowing(){

        return mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode())&&Utility.listIsEmpty(FollowingCompaniesCache.getInstance().getFollowingCompanies());
    }

    private void reloadSearchViewAndData(){

        if(Utility.isEmptyString(mSearchQuery)) {

            setCleanupButtonWithQuery(mSearchQuery);
            ((MainTabHostActivity)getActivity()).hideKeyboard(fSearchQueryEditText);

        }else{

            ((MainTabHostActivity)getActivity()).showKeyBoard(fSearchQueryEditText);
        }

        loadDataByQuery(mSearchQuery);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        if(searchPurpose == SearchPurpose.POST) {

            ((MainTabHostActivity) getActivity()).hideActionBarMenuItems(menu);

            MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
            overFlowMenu.setVisible(false);

            MenuItem searchBox = menu.findItem(R.id.search_box);
            searchBox.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(searchPurpose == SearchPurpose.POST) {
            MainTabHostActivity mainActivity = (MainTabHostActivity) getActivity();

            switch (item.getItemId()) {
                case android.R.id.home:

                    //clean up search text
                    fSearchQueryEditText.setText("");

                    mainActivity.popCurrentFragment();

                    //hide go back button
                    mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);

                    mainActivity.showIndustryNavigatorWithoutLoading();

               return false;

            }
        }

        return false;
    }



    private void setSearchView(){

        fCompanyListView = (ListView) rootView.findViewById(android.R.id.list);

        fSearchView = rootView.findViewById(R.id.search_view);
        fSearchQueryEditText = (EditText) fSearchView.findViewById(R.id.search_query_edit_text);
        fCleanupSearchButton = (ImageView) fSearchView.findViewById(R.id.search_cleanup_button);

        hotCompaniesSectionView = View.inflate(getActivity().getBaseContext(),R.layout.hot_companies_section_in_search,null);
        hotCompaniesSectionText = (TextView) hotCompaniesSectionView.findViewById(R.id.hot_companies_section_text);

        if(mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode())){

             hotCompaniesSectionText.setText(R.string.hot_companies_section_following);
        }

        ImageView searchIcon = (ImageView) fSearchView.findViewById(R.id.search_icon);
        Drawable searchIconDrawable = searchIcon.getDrawable();
        searchIconDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchIcon.setImageDrawable(searchIconDrawable);

        fSearchQueryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                noFollowingCompanyMessage.setVisibility(View.GONE);

                mSearchQuery = s.toString();
                mSearchQuery = mSearchQuery.trim();

                setCleanupButtonWithQuery(mSearchQuery);

                loadDataByQuery(mSearchQuery);

                Utility.trackParseEvent(AnalyticsConstants.SEARCH_IN_COMPANY_TAB, null, null,null);
            }
        });


        fCleanupSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isEmptyString(mSearchQuery)){
                    mSearchQuery = null;
                    fSearchQueryEditText.setText("");
                }
            }
        });


    }

    private void setCleanupButtonWithQuery(String searchQuery){

        if(!Utility.isEmptyString(searchQuery)){
            fCleanupSearchButton.setVisibility(View.VISIBLE);

        }else{

            fCleanupSearchButton.setVisibility(View.GONE);
        }

    }


    //match companyNameObjectIdPairs
    private void loadDataByQuery(String query) {

        List<Company> matchedCompanies = null;
        if(Utility.isEmptyString(query)){

            if(mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode())){

                matchedCompanies = copyFollowingCompanies();

            }else{
                matchedCompanies = copyHotCompanies();
            }

        }else{

            matchedCompanies = Utility.SearchCompanyInCache(query);

        }

        updateCompanyListView(matchedCompanies);
    }


    @Override
    protected void handleLocalBroadcast(Context context, Intent intent)
    {
        String intentAction = intent.getAction();

        if(intentAction.equals(Constants.UPDATE_HOT_COMPANIES_VIEW))
        {
            reloadSearchViewAndData();
        }
    }


    // look into this company
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        if(searchPurpose == SearchPurpose.LOOK){

            setOnListItemClickAsLook(l,v,position,id);

        }else if(searchPurpose == SearchPurpose.POST){

            setOnListItemClickAsPost(l,v,position,id);

        }

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fSearchQueryEditText.getWindowToken(), 0);

    }

    private void setOnListItemClickAsLook(ListView l, View v, int position, long id){

        if (!SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.COMPANY_IS_CLICKED)) {
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

                            CompanyPostsFragment companyPostsFragment = CompanyPostsFragment.newInstance(selectedCompany, ComponentSource.HotCompaniesFragment, getPostsByCompanyBeforeCreatedTimeTask, null);

                            ((MainTabHostActivity) getActivity()).pushFragmentByNewThread(handler, companyPostsFragment, android.R.id.tabhost, 0, PostCommentsFragment.FRAGMENT_TAG);

                        }
                    });
                }
            };
            new Thread(runnable).start();
        }
    }

    private void setOnListItemClickAsPost(ListView l, View v, int position, long id){
        if (!SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.COMPANY_IS_CLICKED)) {
            super.onListItemClick(l, v, position, id);
            SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.COMPANY_IS_CLICKED, true);


            Company selectedCompany  = (Company) l.getItemAtPosition(position);

            getActivity().getActionBar().setTitle(selectedCompany.getName());
            MakePostFragment makeAPostFragment = new MakePostFragment().newInstance(selectedCompany, ComponentSource.SearchFragment);
            //not pop back in case user doesn't post, pop back twice if users do make a post
            ((MainTabHostActivity) getActivity()).pushFragmentByNewThread(handler, makeAPostFragment, android.R.id.tabhost, 0, MakePostFragment.FRAGMENT_TAG);


        }

    }

    //refresh the data when user re-enters it

    @Override
    public void onResume(){

        super.onResume();

        reloadHotCompanies();
    }

    @Override
    public void onPause(){

        super.onPause();

        ((MainTabHostActivity)getActivity()).hideKeyboard(fSearchQueryEditText);

        reloadHotCompanies();
    }

    private void reloadHotCompanies() {

        //reload it again if it's null
      if(Utility.listIsEmpty(HotCompaniesCache.getInstance().getHotCompanies())||mainActivity.isReloadHotCompanyDataDueToRestart()){

            mainActivity.setReloadHotCompanyDataDueToRestart(false);
            List<Company> hotCompanies;

            try {

                //at following filter, hot companies = following companies
                if(mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode())){

                    hotCompanies = FollowingCompaniesCache.getInstance().getFollowingCompanies();
                    if(Utility.listIsEmpty(hotCompanies)){

                        hotCompanies = new GetFollowingCompaniesByUserTask().execute(ParseUser.getCurrentUser()).get();
                    }

                }else{

                    hotCompanies =  new GetHotCompaniesByIndustryCodeTask().execute(mIndustryCode).get();
                }


                if(!Utility.listIsEmpty(hotCompanies)) {

                    HotCompaniesCache.getInstance().setHotCompanies(hotCompanies);
                    updateCompanyListView(HotCompaniesCache.getInstance().getHotCompanies());
                }

            } catch (Exception e) {
                Log.e("Failed to query the latest hot companies by industry when reloading" + mIndustryCode, e.toString());

            }
      }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {


        Bundle bundle = new Bundle();

        bundle.putSerializable(SEARCH_PURPOSE, searchPurpose);

        bundle.putString(SEARCH_QUERY, mSearchQuery);

        savedInstanceState.putBundle(STATE,bundle);

        super.onSaveInstanceState(savedInstanceState);
    }


    // the logic of hotCompanySection headerview and no post message is here...
    private void updateCompanyListView(List<Company> companies){

        if(isFollowingIndustryAndNoFollowing() && Utility.listIsEmpty(companies) && Utility.isEmptyString(mSearchQuery)){

            noFollowingCompanyMessage.setVisibility(View.VISIBLE);
            fNoSearchResultLayout.setVisibility(View.GONE);

        } else if(Utility.listIsEmpty(companies)) {

            fNoSearchResultLayout.setVisibility(View.VISIBLE);
            noFollowingCompanyMessage.setVisibility(View.GONE);

        }else{
            if(Utility.isEmptyString(mSearchQuery)) {
                //otherwise exception ....can't add header after adapter is already set
                mAdapter = null;
                setListAdapter(null);
                //always remove before adding, otherwise space will show up
                fCompanyListView.removeHeaderView(hotCompaniesSectionView);
                fCompanyListView.addHeaderView(hotCompaniesSectionView,"",false);

            }else{

              fCompanyListView.removeHeaderView(hotCompaniesSectionView);
            }
            fNoSearchResultLayout.setVisibility(View.GONE);
            noFollowingCompanyMessage.setVisibility(View.GONE);
        }


        //need to use notifyDataSetChanged() otherwise resetting mAdapter will let headerView, search box also reset which caused to delayed
        if (mAdapter == null) {
            mAdapter = new CompanyListAdapter(getActivity().getBaseContext(), android.R.layout.simple_list_item_multiple_choice, companies, true, showFollowIcon,showFollowIcon);
            setListAdapter(mAdapter);
        } else {

            mAdapter.clear();
            mAdapter.addAll(companies);
            mAdapter.notifyDataSetChanged();
        }
    }


    //to solve notifyDataSetChanged also change hotCompaniesCache or followingCompaniesCache( when industry code is following) bug
    //http://stackoverflow.com/questions/3669325/notifydatasetchanged-example/5092426#5092426
    private List<Company> copyHotCompanies(){

        List<Company> hotCompanies = new ArrayList<Company>();

        for(Company company : HotCompaniesCache.getInstance().getHotCompanies()){
                hotCompanies.add(company);
        }

        return  hotCompanies;
    }

    private List<Company> copyFollowingCompanies(){

        List<Company> followingCompanies = new ArrayList<Company>();

        for(Company company : FollowingCompaniesCache.getInstance().getFollowingCompanies()){
            followingCompanies.add(company);
        }

        return  followingCompanies;
    }



    private void setCreateCompanyButton(){

        fCreateCompanyButton = (Button) rootView.findViewById(R.id.user_create_company_button);

        fCreateCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserCreateCompanyFragment userCreateCompanyFragment = UserCreateCompanyFragment.newInstance(ComponentSource.SearchFragment);
                ((MainTabHostActivity)getActivity()).pushFragmentByNewThread(handler, userCreateCompanyFragment, android.R.id.tabhost, 0, UserCreateCompanyFragment.FRAGMENT_TAG);

            }
        });

    }


    private void setAsSearchToPostSection(){

        showFollowIcon = false;
        rootView.setBackground(getResources().getDrawable(R.drawable.background_color));
        setSearchToPostActionBar();

    }

    private void setSearchToPostActionBar(){

        setHasOptionsMenu(true);

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setTitle(getString(R.string.search_to_post_title));
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

    }



    public enum SearchPurpose{
        POST,
        LOOK;
    }

}

