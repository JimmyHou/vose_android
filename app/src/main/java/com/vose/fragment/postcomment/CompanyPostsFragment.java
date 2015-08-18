package com.vose.fragment.postcomment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.voice.app.R;
import com.parse.ParseUser;
import com.vose.AsyncTask.GetCommentsByParentPostAfterCreatedTimeTask;
import com.vose.AsyncTask.GetCompanyByIdTask;
import com.vose.AsyncTask.GetPostsByCompanyBeforeCreatedTimeTask;
import com.vose.AsyncTask.PostIsLikedByUserTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.adapter.PostListAdapter;
import com.vose.cache.CompanyCheckTimeMapCache;
import com.vose.cache.NewCreatedPostsCache;
import com.vose.data.model.util.ComponentSource;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;
import com.vose.fragment.makepost.MakePostFragment;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.SwipeRefreshUtility;
import com.vose.util.Utility;


import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/20.
 */
public class CompanyPostsFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String LOG_TAG = "CompanyPostsFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_COMPANY_POSTS";


    private View rootView;
    private ListView fPostListView;
    private SwipeRefreshLayout fSwipeRefreshLayout;
    private View noPostView;
    private MainTabHostActivity mActivity;


    private static Company mSelectedCompany;
    private static ComponentSource mComponentSource;
    List<Post> companyPosts = null;
    private PostListAdapter mAdapter;
    private boolean isAdapterCreated = false;
    private boolean reachedTheEndOfPosts = false;
    private Post mLastPost;
    private Post mNewCreatedPost;
    private boolean isUserScrolled;
    private GetPostsByCompanyBeforeCreatedTimeTask getPostsByCompanyBeforeCreatedTimeTask;
    private Handler handler;
    private View progressSection;

    //used to join with new updated posts to make sure the new created post is in it
    // it's slow for parse to update its backend...
    List<Post> currentCompanyPosts = null;


    private final String STATE = "state";
    private final String SELECTED_COMPANY_ID = "selectedCompanyId";
    private final String COMPONENT_SOURCE = "component_source";

     //need to pass in update task since we have no cache for company posts yet, may use a hashmap to store it later on?
    public static CompanyPostsFragment newInstance(Company selectedCompany, ComponentSource componentSource, GetPostsByCompanyBeforeCreatedTimeTask getPostsByCompanyBeforeCreatedTimeTask, Post newCreatedPost){

        CompanyPostsFragment companyPostListFragment = new CompanyPostsFragment();
        //can't use this.mSelectedCompany to set, it should be attached to this object otherwise it's not set
        companyPostListFragment.mSelectedCompany = selectedCompany;
        companyPostListFragment.getPostsByCompanyBeforeCreatedTimeTask = getPostsByCompanyBeforeCreatedTimeTask;
        companyPostListFragment.mComponentSource = componentSource;
        companyPostListFragment.mNewCreatedPost = newCreatedPost;

        if(newCreatedPost!=null) {
            NewCreatedPostsCache.getInstance().setNewPostToCompany(selectedCompany.getName(), newCreatedPost);

        }

        return companyPostListFragment;

    }

    //recreate the state
    @Override
    public void onCreate(Bundle savedInstanceState){

        if(savedInstanceState!=null){

            Bundle state = (Bundle) savedInstanceState.get(STATE);

            String companyId =  state.getString(SELECTED_COMPANY_ID);

            try {
                mSelectedCompany = new GetCompanyByIdTask().execute(companyId).get();
            } catch (Exception e) {
                Log.e("Failed to query company by objectId " + ParseUser.getCurrentUser().getObjectId(), e.toString());

            }

            mComponentSource = (ComponentSource) state.get(COMPONENT_SOURCE);
            getPostsByCompanyBeforeCreatedTimeTask = new GetPostsByCompanyBeforeCreatedTimeTask(new Date());
            getPostsByCompanyBeforeCreatedTimeTask.execute(mSelectedCompany);

         }


        super.onCreate(savedInstanceState);
    }



    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //handle no post case
        rootView = inflater.inflate(R.layout.fragment_company_posts, container, false);
        progressSection = rootView.findViewById(R.id.progressSection);
        noPostView =  rootView.findViewById(R.id.company_no_post_message);
        handler = new Handler();


        setupListViewAndSwipeRefresh();
        //set the action bar menu in the end of onCreteView, only required in fragment action bar not in activity
        setActionBar();

        //update the company check out time for company list checkmark
        CompanyCheckTimeMapCache.getInstance(getActivity()).updateCompanyCheckTimeMap(mSelectedCompany.getObjectId());
        return rootView;
    }


    private void setActionBar(){

        mActivity.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        setHasOptionsMenu(true);

        mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity.getActionBar().setTitle(Utility.makeActionBarTitle(mSelectedCompany.getName()));
    }


    @Override
    public void onStart(){

        Utility.trackParseEvent(AnalyticsConstants.COMPANY_POSTS_VIEW, AnalyticsConstants.COMPANY_NAME, mSelectedCompany.getName(),mComponentSource.getParseCode());

        super.onStart();


        if(Utility.listIsEmpty(companyPosts)) {

            runCompanyPostsUpdateTask();
        }else{
            progressSection.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainTabHostActivity) activity;
    }


    @Override
    public void onPause(){
        //put the tabs back when the back button is pressed
        SharedPreferencesManager.getInstance(mActivity).putBoolean(Constants.COMPANY_IS_CLICKED, false);

        super.onPause();
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        // pass the company name to the next activity, cast to String temporarily, should actually pass in the Company entity!
        Post selectedPost = (Post) l.getItemAtPosition(position);
        selectedPost.setCompany(mSelectedCompany);

        //need to pass in a very old time
        GetCommentsByParentPostAfterCreatedTimeTask getCommentsByParentPostAfterCreatedTimeTask = new GetCommentsByParentPostAfterCreatedTimeTask(new Date(0));
        getCommentsByParentPostAfterCreatedTimeTask.execute(selectedPost);

        PostIsLikedByUserTask postIsLikedByUserTask = new PostIsLikedByUserTask();
        postIsLikedByUserTask.execute(selectedPost);

        PostCommentsFragment postCommentListFragment = PostCommentsFragment.newInstance(selectedPost, v, ComponentSource.CompanyPostsFragment, getCommentsByParentPostAfterCreatedTimeTask, postIsLikedByUserTask);
        mActivity.pushFragmentByNewThread(handler, postCommentListFragment, android.R.id.tabhost, 0, PostCommentsFragment.FRAGMENT_TAG);

      }



    @Override
    public void onRefresh() {
        //trigger refresh here
        if(fSwipeRefreshLayout !=null){
            fSwipeRefreshLayout.setRefreshing(true);
            isUserScrolled = true;
        }

        try {
            companyPosts = new GetPostsByCompanyBeforeCreatedTimeTask(new Date()).execute(mSelectedCompany).get();
            if(mNewCreatedPost !=null) {

                companyPosts =  Utility.distinctJoinNewPosts(null, companyPosts, NewCreatedPostsCache.getInstance().getPostsByCompany(mSelectedCompany.getName()));
            }

            updatePostsView(companyPosts);
        }  catch (Exception e) {
            Log.e("Failed to swipe new posts by user " + ParseUser.getCurrentUser() + " for company " + mSelectedCompany.getObjectId(), e.toString());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    //set up action bar items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        mActivity.hideActionBarMenuItems(menu);

        MenuItem postActionFromCompanyPostsFragment = menu.findItem(R.id.action_make_post_company_posts_fragment);
        postActionFromCompanyPostsFragment.setVisible(true);


        super.onCreateOptionsMenu(menu, inflater);
    }


    //"Return false to allow normal menu processing to proceed, true to consume it here."
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case android.R.id.home:


                if(mComponentSource == ComponentSource.HotVoicesFragment ) {

                    mActivity.goBackToCompanyTabHostActivity(handler);
                    mActivity.showIndustryNavigatorWithoutLoading();
                    mActivity.getActionBar().setDisplayHomeAsUpEnabled(false);


                }else if(mComponentSource == ComponentSource.HotCompaniesFragment || mComponentSource == ComponentSource.FavoriteCompaniesFragment ||  mComponentSource == ComponentSource.MainTabHostActivity){

                    mActivity.popCurrentFragment();
                    mActivity.showIndustryNavigatorWithoutLoading();

                }else if(mComponentSource == ComponentSource.SearchFragment){

                    mActivity.popCurrentFragment();
                    mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);

                }else if(mComponentSource ==ComponentSource.CompanyPostsFragment){

                     ((MainTabHostActivity)getActivity()).popCurrentFragment();

                    ((MainTabHostActivity)getActivity()).showIndustryNavigatorWithoutLoading();
                }

                return false;


            case R.id.action_make_post_company_posts_fragment:

                mActivity.getActionBar().setTitle(mSelectedCompany.getName());

                MakePostFragment makeAPostFragment = new MakePostFragment().newInstance(mSelectedCompany, ComponentSource.CompanyPostsFragment);
                //not pop back in case user doesn't post, pop back twice if users do make a post
                mActivity.pushFragmentByNewThread(handler, makeAPostFragment, android.R.id.tabhost, 0, MakePostFragment.FRAGMENT_TAG);


                return false;
        }

        return false;
    }

    //save the data when for recreating fragment!
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Bundle bundle = new Bundle();

        //can't save Company since it's a ParseObject, when retrieve it, all the member variables will be lost
        bundle.putString(SELECTED_COMPANY_ID, mSelectedCompany.getObjectId());

        bundle.putSerializable(COMPONENT_SOURCE,mComponentSource);

        savedInstanceState.putBundle(STATE,bundle);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }





    public void runCompanyPostsUpdateTask(){

        // use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        try {


                            companyPosts = getPostsByCompanyBeforeCreatedTimeTask.get();

                            if(mNewCreatedPost !=null) {
                                //add to the head!
                                //Utility.addNewPostToTop(companyPosts, mNewCreatedPost);

                              //  companyPosts =  Utility.distinctJoinNewPosts(companyPosts, NewCreatedPostsCache.getInstance().getPostsByCompany(mSelectedCompany.getName()));
                            }

                            companyPosts =  Utility.distinctJoinNewPosts(null, companyPosts, NewCreatedPostsCache.getInstance().getPostsByCompany(mSelectedCompany.getName()));


                            updateView(companyPosts);

                        } catch (Exception e) {
                            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
                        }

                    }
                });
            }
        };
        new Thread(runnable).start();
    }





    private void updateView(List<Post> companyPosts){

        progressSection.setVisibility(View.GONE);

        if(Utility.listIsEmpty(companyPosts)){
               noPostView.setVisibility(View.VISIBLE);
        }else {

            noPostView.setVisibility(View.GONE);

            mAdapter = new PostListAdapter(mActivity, android.R.layout.simple_list_item_multiple_choice, companyPosts, ComponentSource.CompanyPostsFragment);
            setListAdapter(mAdapter);
        }

        stopSwipeRefreshingBar();
    }


    private void updatePostsView(List<Post> companyPosts){

        isAdapterCreated = true;
        mAdapter = new PostListAdapter(mActivity, android.R.layout.simple_list_item_multiple_choice, companyPosts, ComponentSource.CompanyPostsFragment);
        setListAdapter(mAdapter);

        if (noPostView != null)
            noPostView.setVisibility(View.GONE);

        stopSwipeRefreshingBar();
    }



    private void setupListViewAndSwipeRefresh(){
        fPostListView = (ListView) rootView.findViewById(android.R.id.list);
        fSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.hot_voice_swipe_container);

        SwipeRefreshUtility.setup(fSwipeRefreshLayout, this);

        fPostListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isUserScrolled = true;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //set up scroll up and down respectively
                setScrollUpRefreshLayout();
                if(isUserScrolled) {
                    setScrollDownUpdate(firstVisibleItem, visibleItemCount, totalItemCount);
                }
                isUserScrolled = false;
            }
        });
    }

    private void setScrollUpRefreshLayout(){
        boolean isListEmpty = (fPostListView == null || fPostListView.getChildCount() == 0);
        int topRowVerticalPosition = isListEmpty ? 0: fPostListView.getChildAt(0).getTop();
        fSwipeRefreshLayout.setEnabled(topRowVerticalPosition >=0);
    }


    private void setScrollDownUpdate(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        if(companyPosts == null || companyPosts.size() == 0)
            return;
        //the post also counts for visibleItemCount
        final int lastItem = firstVisibleItem + visibleItemCount;
        // set up scroll down, first time to reached end of list, load more data
        if (isAdapterCreated && lastItem == totalItemCount && !reachedTheEndOfPosts) {
            mLastPost = companyPosts.get(companyPosts.size() - 1);
            try {

                List<Post> newLoadedPosts = new GetPostsByCompanyBeforeCreatedTimeTask(mLastPost.getCreatedAt()).execute(mSelectedCompany).get();
                //check if this is the last comment
                if (mLastPost.getObjectId().equals(newLoadedPosts.get(newLoadedPosts.size() - 1).getObjectId())) {
                    reachedTheEndOfPosts = true;
                } else {
                    companyPosts.addAll(newLoadedPosts);
                    mAdapter.notifyDataSetChanged();
                    mLastPost = newLoadedPosts.get(newLoadedPosts.size() - 1);
                    reachedTheEndOfPosts = false;
                }
            } catch (Exception e) {
                Log.e("Failed to scroll older posts by user " + ParseUser.getCurrentUser() + " for company " + mSelectedCompany.getObjectId(), e.toString());
            }
        }

    }

    private void stopSwipeRefreshingBar(){
        if(fSwipeRefreshLayout !=null){
            fSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
