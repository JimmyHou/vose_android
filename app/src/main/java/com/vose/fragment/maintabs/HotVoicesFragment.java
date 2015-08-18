package com.vose.fragment.maintabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.AsyncTask.GetCommentsByParentPostAfterCreatedTimeTask;
import com.vose.AsyncTask.GetFollowingCompaniesByUserTask;
import com.vose.AsyncTask.PostIsLikedByUserTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.adapter.PostListAdapter;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.cache.HotCompaniesCache;
import com.vose.cache.HotVoicesCache;
import com.vose.cache.NewCreatedPostsCache;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;
import com.vose.data.model.util.ComponentSource;
import com.vose.data.model.util.ExtraIndustry;
import com.vose.fragment.postcomment.PostCommentsFragment;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.SwipeRefreshUtility;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;
/**
 * Created by jimmyhou on 2014/8/13.
 */

public class HotVoicesFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "HotVoicesFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_HOT_VOICES";

    //-----------
    // Member vars
    //-------------
    private ListView fHotVoiceListView = null;
    private PostListAdapter adapter = null;
    private SwipeRefreshLayout fSwipeRefreshLayout;
    private View rootView;
    private Handler handler = null;
    //default as software in this case
    private String mIndustryCode;
    private List<Company> mHotCompanies;
    private boolean reachedTheEndOfPosts = false;
    private Post mLastPost;
    private List<Post> hotVoices;
    private boolean isUserScrolled;
    private View fNoFollowingCompanyMessage = null;
    private View progressSection;


    //hotVoices is preloaded into HotVoices SingleToneIntance
    // use newInstance as factory pattern

    public static HotVoicesFragment newInstance(){
        return new HotVoicesFragment();
    }

    public HotVoicesFragment(){
        super();
    }

    //not loading again
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mIndustryCode = SharedPreferencesManager.getInstance(getActivity()).getString(Constants.INTERESTED_INDUSTRY_CODE, "software");

        rootView = inflater.inflate(R.layout.fragment_hot_voices, container, false);
        fNoFollowingCompanyMessage =  rootView.findViewById(R.id.no_following_company_message);
        fNoFollowingCompanyMessage.setVisibility(View.GONE);

        progressSection = rootView.findViewById(R.id.progressSection);

        // comment it out since in opening push notification, the notified fragment
        // will be called before initializing HotVoicesFrgment
        //getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);


        mHotCompanies = HotCompaniesCache.getInstance().getHotCompanies();

       if(mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode())) {

           List<Company> followingCompanies = FollowingCompaniesCache.getInstance().getFollowingCompanies();

           HotCompaniesCache.getInstance().setHotCompanies(followingCompanies);

           mHotCompanies = followingCompanies;

           if (hasNoFollowingInFollowingIndustry(followingCompanies)) {

               fNoFollowingCompanyMessage .setVisibility(View.VISIBLE);

               progressSection.setVisibility(View.GONE);

           }else{
               fNoFollowingCompanyMessage .setVisibility(View.GONE);
           }
       }



        handler = new Handler();
        setupListViewAndSwipeRefresh();

       return rootView;
    }

    private boolean hasNoFollowingInFollowingIndustry(List<Company> followingCompanies){
        return Utility.listIsEmpty(followingCompanies) && mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode());
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // pass the company name to the next activity, cast to String temporarily, should actually pass in the Company entity!
        final Post selectedPost = (Post) l.getItemAtPosition(position);
        //SharedPreferencesManager.getInstance(getActivity()).setPreferenceInteger(Constants.HOT_VOICES_ITEM_CLICK_POSITION, position);


        //need to pass in a very old time
        final GetCommentsByParentPostAfterCreatedTimeTask getCommentsByParentPostAfterCreatedTimeTask = new GetCommentsByParentPostAfterCreatedTimeTask(new Date(0));
        getCommentsByParentPostAfterCreatedTimeTask.execute(selectedPost);

        final PostIsLikedByUserTask postIsLikedByUserTask = new PostIsLikedByUserTask();
        postIsLikedByUserTask.execute(selectedPost);

        PostCommentsFragment postCommentListFragment = PostCommentsFragment.newInstance(selectedPost, v, ComponentSource.HotVoicesFragment, getCommentsByParentPostAfterCreatedTimeTask, postIsLikedByUserTask);
        ((MainTabHostActivity)getActivity()).pushFragmentByNewThread(handler, postCommentListFragment, android.R.id.tabhost, 0, PostCommentsFragment.FRAGMENT_TAG);
    }

    @Override
    public  void onPause(){
        super.onPause();
     }


    //Fragment has no restart(), only in activity
    @Override
    public  void onStart(){

        Utility.trackParseEvent(AnalyticsConstants.VOICE_TAB, null, null, null);

        super.onStart();
        //don't update everytime when user comes back, only update the list when refreshing and relog in
        if(Utility.listIsEmpty(hotVoices) || (mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode()) && Utility.listIsEmpty(FollowingCompaniesCache.getInstance().getFollowingCompanies()))){

            runHotVoicesUpdateTask();

        }else{

            progressSection.setVisibility(View.GONE);
            //fNoFollowingCompanyMessage.setVisibility(View.GONE);
        }
    }



    @Override
    protected void handleLocalBroadcast(Context context, Intent intent)
    {
        String intentAction = intent.getAction();

        if(intentAction.equals(Constants.UPDATE_HOT_VOICES_VIEW) && !mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode()))
        {
            updateListView(HotVoicesCache.getInstance().getHotVoices());

        }
    }


    @Override
    public void onRefresh() {
        //trigger refresh here
        if(fSwipeRefreshLayout !=null){
            fSwipeRefreshLayout.setRefreshing(true);
            isUserScrolled = true;
        }

        runHotVoicesUpdateTask();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        ((MainTabHostActivity)getActivity()).hideActionBarMenuItems(menu);

        MenuItem post = menu.findItem(R.id.post);
        post.setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }


    public void runHotVoicesUpdateTask(){

        //the position return -1 means shifting back from other tabs
       // final int pos = SharedPreferencesManager.getInstance(getActivity()).getInteger(Constants.HOT_VOICES_ITEM_CLICK_POSITION, -1);

        // use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        if(mIndustryCode.equals(ExtraIndustry.FOLLOWING.getCode()) && Utility.listIsEmpty(mHotCompanies)){

                            try {
                                mHotCompanies = new GetFollowingCompaniesByUserTask().execute(ParseUser.getCurrentUser()).get();
                                FollowingCompaniesCache.getInstance().setFollowingCompanies(mHotCompanies);

                            } catch (Exception ex) {
                                Log.e(LOG_TAG, ex.toString());
                            }
                        }

                        hotVoices = Utility.loadHotVoices(mIndustryCode, new Date(), LOG_TAG);
                        //in the new created post is not stored at backend yet so can't load it right now
                        hotVoices = Utility.distinctJoinNewPosts(mIndustryCode, hotVoices, NewCreatedPostsCache.getInstance().getNewCreatedPosts());


                        HotVoicesCache.getInstance().setHotVoices(hotVoices);

                        updateListView(hotVoices);

                        progressSection.setVisibility(View.GONE);

                    }
                });
            }
        };
        new Thread(runnable).start();
    }


    private void updateListView(List<Post> hotVoices){

        if(Utility.listIsEmpty(hotVoices) && hasNoFollowingInFollowingIndustry(mHotCompanies)){

            rootView.findViewById(R.id.no_hot_voices_message).setVisibility(View.GONE);
            fNoFollowingCompanyMessage.setVisibility(View.VISIBLE);

        }else if(Utility.listIsEmpty(hotVoices)){

            rootView.findViewById(R.id.no_hot_voices_message).setVisibility(View.VISIBLE);
            fNoFollowingCompanyMessage.setVisibility(View.GONE);

        }else {

            rootView.findViewById(R.id.no_hot_voices_message).setVisibility(View.GONE);
            fNoFollowingCompanyMessage.setVisibility(View.GONE);
        }



        adapter = new PostListAdapter((MainTabHostActivity)getActivity(), android.R.layout.simple_list_item_multiple_choice, hotVoices, ComponentSource.HotVoicesFragment);
        setListAdapter(adapter);
        stopSwipeRefreshingBar();
    }


    private void setupListViewAndSwipeRefresh(){
        fHotVoiceListView = (ListView) rootView.findViewById(android.R.id.list);
        fSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.hot_voice_swipe_container);

        SwipeRefreshUtility.setup(fSwipeRefreshLayout, this);

        fHotVoiceListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isUserScrolled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                setScrollUpRefreshLayout();
                //only read it when user is really scrolling, otherwise onScroll() is called multiple times!
                if(isUserScrolled) {
                    setScrollDownUpdate(firstVisibleItem, visibleItemCount, totalItemCount);
                }
                isUserScrolled = false;
            }
        });

    }


    private void setScrollUpRefreshLayout(){
        boolean isListEmpty = (fHotVoiceListView == null || fHotVoiceListView.getChildCount() == 0);
        int topRowVerticalPosition = isListEmpty ? 0: fHotVoiceListView.getChildAt(0).getTop();
        fSwipeRefreshLayout.setEnabled(topRowVerticalPosition >=0);
    }


    private void setScrollDownUpdate(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        if(hotVoices == null || hotVoices.size() == 0)
            return;

        //the post also counts for visibleItemCount
        final int lastItem = firstVisibleItem + visibleItemCount;
        // set up scroll down, first time to reached end of list, load more data
        if (lastItem == totalItemCount && !reachedTheEndOfPosts) {
            mLastPost = hotVoices.get(hotVoices.size() - 1);
            try {

                //should change it to created time method!!
                List<Post> newLoadedPosts = Utility.loadHotVoices(mIndustryCode,  mLastPost.getCreatedAt(), LOG_TAG);
                //check if this is the last comment
                if (mLastPost.getObjectId().equals(newLoadedPosts.get(newLoadedPosts.size() - 1).getObjectId())) {
                    reachedTheEndOfPosts = true;
                } else {
                    hotVoices.addAll(newLoadedPosts);
                    adapter.notifyDataSetChanged();
                    mLastPost = newLoadedPosts.get(newLoadedPosts.size() - 1);
                    reachedTheEndOfPosts = false;
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, Utility.getExceptionStackTrace(e));
            }
        }

    }

    private void stopSwipeRefreshingBar(){
        if(fSwipeRefreshLayout !=null){
            fSwipeRefreshLayout.setRefreshing(false);
        }
    }

}
