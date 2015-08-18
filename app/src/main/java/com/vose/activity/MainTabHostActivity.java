package com.vose.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.voice.app.R;
import com.vose.AsyncTask.CreateUserFlagPostTask;
import com.vose.AsyncTask.GetAllCompaniesTask;
import com.vose.AsyncTask.GetCommentsByParentPostAfterCreatedTimeTask;
import com.vose.AsyncTask.GetCompanyByIdTask;
import com.vose.AsyncTask.GetFollowingCompaniesByUserTask;
import com.vose.AsyncTask.GetHotCompaniesByIndustryCodeTask;
import com.vose.AsyncTask.GetHotVoicesByIndustryBeforeCreatedTimeTask;
import com.vose.AsyncTask.GetIndustriesTask;
import com.vose.AsyncTask.GetMostRecentlyLikedPostIdsTask;
import com.vose.AsyncTask.GetPostByIdTask;
import com.vose.AsyncTask.GetPostsByCompanyBeforeCreatedTimeTask;
import com.vose.AsyncTask.GetUserFeedbackReplyTask;
import com.vose.AsyncTask.OnTaskCompleted;
import com.vose.AsyncTask.PostIsLikedByUserTask;
import com.vose.AsyncTask.UpdatePostIntoDBTask;
import com.vose.adapter.IndustrySpinnerAdapter;
import com.vose.backgroundService.ReloadMainActivityDataService;
import com.vose.cache.AllCompaniesCache;
import com.vose.cache.AlreadyLikedPostIdsCache;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.cache.HotCompaniesCache;
import com.vose.cache.HotVoicesCache;
import com.vose.cache.IndustriesCache;
import com.vose.cache.NewCreatedPostsCache;
import com.vose.core.data.dao.usefeedback.UserFeedbackDAO;
import com.vose.core.data.dao.usefeedback.UserFeedbackDAOImpl;
import com.vose.core.data.dao.usefeedback.UserFeedbackReplyDAOImpl;
import com.vose.core.data.dao.user.UserDAOImpl;
import com.vose.core.data.service.user.UserService;
import com.vose.core.data.service.user.UserServiceImpl;
import com.vose.core.data.service.userfeedback.UserFeedbackReplyService;
import com.vose.core.data.service.userfeedback.UserFeedbackReplyServiceImpl;
import com.vose.core.data.service.userfeedback.UserFeedbackService;
import com.vose.core.data.service.userfeedback.UserFeedbackServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.Industry;
import com.vose.data.model.company.ParseIndustry;
import com.vose.data.model.post.Post;
import com.vose.data.model.userfeedback.FlagReason;
import com.vose.data.model.userfeedback.UserFeedbackReply;
import com.vose.data.model.util.ComponentSource;
import com.vose.fragment.action.MyPostsFragment;
import com.vose.fragment.action.RulesFragment;
import com.vose.fragment.action.UserCreateCompanyFragment;
import com.vose.fragment.maintabs.FavoriteCompaniesFragment;
import com.vose.fragment.maintabs.HotCompaniesFragment;
import com.vose.fragment.maintabs.HotVoicesFragment;
import com.vose.fragment.postcomment.CompanyPostsFragment;
import com.vose.fragment.postcomment.PostCommentsFragment;
import com.vose.notification.ParsePushNotificationConstants;
import com.vose.notification.PushNotificationService;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.CustomDialog;
import com.vose.util.IntentLauncher;
import com.vose.util.ParseDatabaseColumnNames;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/11/8.
 */
public class MainTabHostActivity extends FragmentActivity implements ActionBar.OnNavigationListener, GestureDetector.OnGestureListener,  GestureDetector.OnDoubleTapListener {

    final String LOG_TAG = "MainTabHostActivity";

    private FragmentTabHost mTabHost;
    private UserFeedbackDAO userFeedbackDAO;
    private UserFeedbackService userFeedbackService;
    private Handler handler;
    private IndustrySpinnerAdapter industrySpinnerAdapter;
    private int industrySpinnerStartCounter = 1;
    private String mOldIndustryCode;

    private GestureDetectorCompat mDetector;
    private long lastVisitTime;
    private final long REFRESH_PERIOD = 900*1000;
    private final MainTabHostActivity mainActivity = this;
    private boolean reloadHotCompanyDataDueToRestart = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkUserStatus();

        super.onCreate(savedInstanceState);

        ParseAnalytics.trackAppOpened(getIntent());

        SharedPreferencesManager.getInstance(this).putBoolean(Constants.HAS_LOGGED_IN, true);

        setActionBar();

        //Android & iOS download urls are hard-coded now
        //loadSystemConfig();

        handler = new Handler();

        setContentView(R.layout.activity_main_tabhost);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        setTab(getString(R.string.hot_voices),R.drawable.selector_hot_voices_tab, HotVoicesFragment.class);
        setTab(getString(R.string.hot_companies),R.drawable.selector_hot_companies_tab, HotCompaniesFragment.class);
        setTab(getString(R.string.following),R.drawable.selector_following_companies_tab, FavoriteCompaniesFragment.class);


        //set underline color
        TabWidget tabWidget = mTabHost.getTabWidget();
        int len = tabWidget.getTabCount();
        for (int i = 0; i < len; i++) {
            final View tab = tabWidget.getChildTabViewAt(i);
            tab.setBackground(getResources().getDrawable(R.drawable.selector_tab_underline_color));
        }


        userFeedbackDAO = new UserFeedbackDAOImpl();
        userFeedbackService = new UserFeedbackServiceImpl(userFeedbackDAO);


        //swipe is not working
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);

        mOldIndustryCode = ParseUser.getCurrentUser().getString(Constants.INTERESTED_INDUSTRY_CODE);

        handlePushNotification();

    }

//    private void loadSystemConfig(){
//
//        String androidDownloadURL = SharedPreferencesManager.getInstance(this).getString(ParseDatabaseColumnNames.ANDROID_DOWNLOAD_URL, null);
//        String iosDownloadURL = SharedPreferencesManager.getInstance(this).getString(ParseDatabaseColumnNames.IOS_DOWNLOAD_URL, null);
//
//        if(Utility.isEmptyString(androidDownloadURL)|| Utility.isEmptyString(iosDownloadURL)) {
//
//            ParseConfig.getInBackground(new ConfigCallback() {
//                @Override
//                public void done(ParseConfig config, ParseException e) {
//                    if (e == null) {
//                        Log.d("System Config", "Config was fetched from the server.");
//                    } else {
//                        Log.e("System Config", "Failed to fetch. Using Cached Config.");
//                        config = ParseConfig.getCurrentConfig();
//                    }
//
//                    // Get the message from config or fallback to default value
//                    String androidDownloadURL = config.getString(ParseDatabaseColumnNames.ANDROID_DOWNLOAD_URL, "");
//                    String iosDownloadURL = config.getString(ParseDatabaseColumnNames.IOS_DOWNLOAD_URL,"");
//
//                    SharedPreferencesManager.getInstance(getApplicationContext()).putString(ParseDatabaseColumnNames.ANDROID_DOWNLOAD_URL, androidDownloadURL);
//                    SharedPreferencesManager.getInstance(getApplicationContext()).putString(ParseDatabaseColumnNames.IOS_DOWNLOAD_URL,iosDownloadURL);
//
//                }
//            });
//
//        }
//
//    }


    private void setTab(String tabName, int imageId, Class triggeredFragmentClass){

        View fTabIndicator =  LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.tab_indicator, null);

        ((TextView) fTabIndicator.findViewById(R.id.tab_name)).setText(tabName);
         fTabIndicator.findViewById(R.id.tab_icon).setBackground(getResources().getDrawable(imageId));

        TabHost.TabSpec spec = mTabHost.newTabSpec(tabName).setIndicator(fTabIndicator);


        mTabHost.addTab(spec,triggeredFragmentClass, null);
    }

    private void setActionBar() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        resetActionBar();

        //most to have otherwise action fragment action bar title color is black...
        //The ActionBar title ID is hidden, or in other words, it's internal and accessing it can't be done typically.
        int actionBarTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView abTitle = (TextView) findViewById(actionBarTitleId);
        abTitle.setTextColor(getBaseContext().getResources().getColor(R.color.white));

    }

    private void handlePushNotification(){

        Intent intent = getIntent();
        if(!intent.getBooleanExtra(ParsePushNotificationConstants.IS_NOTIFICATION, false)){
            return;
        }

        //using restoreData to realoadData will lead to company name gone away due to loading message...
        Intent restoreDataIntent = new Intent(this, ReloadMainActivityDataService.class);
        startService(restoreDataIntent);

        String fragmentTag = intent.getStringExtra(Constants.START_FRAGMENT);
        if(fragmentTag == null){
            return;
        }

        if(fragmentTag.equals(CompanyPostsFragment.FRAGMENT_TAG)){

            pushCompanyPostsFragmentByNotification(intent);
        }else if(fragmentTag.equals(PostCommentsFragment.FRAGMENT_TAG)){

            pushPostCommentsFragmentByNotification(intent);
        }

       //need to reset it otherwise the next time start the activity will call this again
       intent.removeExtra(ParsePushNotificationConstants.IS_NOTIFICATION);
       intent.removeExtra(Constants.START_FRAGMENT);
    }




    private void pushCompanyPostsFragmentByNotification(Intent intent){

        String companyId = intent.getStringExtra(ParsePushNotificationConstants.COMPANY_ID);
        Company selectedCompany = findSelectedCompanyById(companyId);
        //go to industry tab
        if(selectedCompany == null){
            return;
        }

        final GetPostsByCompanyBeforeCreatedTimeTask getPostsByCompanyBeforeCreatedTimeTask = new GetPostsByCompanyBeforeCreatedTimeTask(new Date());
        getPostsByCompanyBeforeCreatedTimeTask.execute(selectedCompany);

        CompanyPostsFragment companyPostsFragment = CompanyPostsFragment.newInstance(selectedCompany, ComponentSource.HotCompaniesFragment, getPostsByCompanyBeforeCreatedTimeTask, null);
        pushFragmentByNewThread(handler, companyPostsFragment, android.R.id.tabhost, 0, PostCommentsFragment.FRAGMENT_TAG);
    }

    private void pushPostCommentsFragmentByNotification(Intent intent){

        String postId = intent.getStringExtra(ParsePushNotificationConstants.POST_ID);
        Post parentPost = null;
        try {
            parentPost = new GetPostByIdTask().execute(postId).get();
        } catch (Exception e) {

           Log.e(LOG_TAG, e.toString());
        }

        if(parentPost == null){
            return;
        }

        //need to pass in a very old time
        final GetCommentsByParentPostAfterCreatedTimeTask getCommentsByParentPostAfterCreatedTimeTask = new GetCommentsByParentPostAfterCreatedTimeTask(new Date(0));
        getCommentsByParentPostAfterCreatedTimeTask.execute(parentPost);

        //maybe we can remove it
        final PostIsLikedByUserTask postIsLikedByUserTask = new PostIsLikedByUserTask();
        postIsLikedByUserTask.execute(parentPost);

        PostCommentsFragment postCommentListFragment = PostCommentsFragment.newInstance(parentPost, null,ComponentSource.HotVoicesFragment, getCommentsByParentPostAfterCreatedTimeTask, postIsLikedByUserTask);
        pushFragmentByNewThread(handler, postCommentListFragment, android.R.id.tabhost, 0, PostCommentsFragment.FRAGMENT_TAG);

      }




    @Override
    public void onStart(){

        super.onStart();
        loadFeedbackReplyAndShowDialog();

    }


    @Override
    public void onResume(){

        super.onResume();
        updateParseInstallationInApp(true);
    }

    @Override
    public void onPause(){
        super.onPause();

        lastVisitTime = System.currentTimeMillis();
        updateParseInstallationInApp(false);
        subscribeNewIndustryNameNotification(mOldIndustryCode);

    }

//
//    @Override
//    public void onRestart(){
//
//
////
////        long currentTime =  System.currentTimeMillis();
////
////        if((currentTime - lastVisitTime) >= REFRESH_PERIOD) {
////
////            restoreDataSection();
////        }
//
//        super.onRestart();
//    }


    private void updateParseInstallationInApp(boolean isApp){

        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put(Constants.IN_APP, isApp);
        parseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
    }

    //restart won't be called and it's children fragments will be
    //restored earlier than activity if we leave from a child fragment
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        getIntent().removeExtra(ParsePushNotificationConstants.IS_NOTIFICATION);
        getIntent().removeExtra(Constants.START_FRAGMENT);

        restoreDataSection();
        setReloadHotCompanyDataDueToRestart(true);

        super.onRestoreInstanceState(savedInstanceState);

    }



    @Override
    public void onStop(){

        super.onStop();

        unregisterBroadcastReceivers();
    }


    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            handleLocalBroadcast(context, intent);
        }
    };

    protected void handleLocalBroadcast(Context context, Intent intent)
    {
       String intentAction = intent.getAction();

       if(intentAction.equals(Constants.APP_BECOME_ACTIVE)) {
           //runReloadDataTask();
           runReloadDataService();

        }else if(intentAction.equals(Constants.UPDATE_MAIN_ACTIVITY_INDUSTRY_NAVIGATOR)){

           setIndustrySpinnerForActionBar();
       }
    }

    protected void registerBroadcastReceivers()
    {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.APP_BECOME_ACTIVE));
        lbm.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.UPDATE_MAIN_ACTIVITY_INDUSTRY_NAVIGATOR));
    }

    protected void unregisterBroadcastReceivers()
    {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mBroadcastReceiver);
    }


    public void runReloadDataService(){

        //reset it after reloading data
        setIndustrySpinnerWithoutLoading();

        Intent restoreDataIntent = new Intent(this, ReloadMainActivityDataService.class);
        startService(restoreDataIntent);

        mTabHost.setVisibility(View.VISIBLE);

    }

    public void runReloadDataTask(){

        // use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {

                       boolean isIndustriesEmpty = Utility.listIsEmpty(IndustriesCache.getInstance().getIndustries())
                               || Utility.listIsEmpty(IndustriesCache.getInstance().getIndustryNames());


                       reloadData();

                       //reset it after reloading data
                       if(isIndustriesEmpty && getActionBar().getNavigationMode()  == ActionBar.NAVIGATION_MODE_LIST){
                           setIndustrySpinnerForActionBar();
                       }

                       sendFragmentsUpdateBroadcast();

                       mTabHost.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        new Thread(runnable).start();
    }


    public void restoreDataSection(){

            registerBroadcastReceivers();

            //hide empty view when the data is missing...
            mTabHost.setVisibility(View.GONE);

            //need to use broadcast receiver can't just call runReloadDataTask();
            //otherwise the UI thread will be block, need to wait until the data loading is done...
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.APP_BECOME_ACTIVE));
    }


    private void reloadData(){

        FollowingCompaniesCache.getInstance().clear();
        HotVoicesCache.getInstance().clear();
        HotCompaniesCache.getInstance().clear();


        List<Company> followingCompanies = null;

        try {
            followingCompanies = new GetFollowingCompaniesByUserTask().execute(ParseUser.getCurrentUser()).get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }

        FollowingCompaniesCache.getInstance().setFollowingCompanies(followingCompanies);


        List<String> alreadyLikedPostIds = null;


        try {
            alreadyLikedPostIds = new GetMostRecentlyLikedPostIdsTask().execute(ParseUser.getCurrentUser()).get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }

        AlreadyLikedPostIdsCache.getInstance().setAlreadyLikedPostIds(alreadyLikedPostIds);


        //use get since need to make sure hot companies is loaded before the next activity is started

        List<ParseIndustry> industries = null;
        try {
            industries =  new GetIndustriesTask(this).execute("").get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(ex));
        }

        IndustriesCache.getInstance().setIndustries(industries);

        if( getActionBar().getNavigationMode()  == ActionBar.NAVIGATION_MODE_LIST) {
            setIndustrySpinnerForActionBar();
        }



        String industryCode = (String) ParseUser.getCurrentUser().get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);
        List<Company> hotCompanies = null;
        try {
            hotCompanies =  new GetHotCompaniesByIndustryCodeTask().execute(industryCode).get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }


        //need to explicitly reassign here since onPostExecute() is hit late even after HotVoicesFragment is called...
        HotCompaniesCache.getInstance().setHotCompanies(hotCompanies);

        List<Post> hotVoices = Utility.loadHotVoices(industryCode, new Date(), LOG_TAG);

        HotVoicesCache.getInstance().setHotVoices(hotVoices);


        List<Company> allCompanies = null;

        try {
            allCompanies=  new GetAllCompaniesTask(this).execute().get();
        }catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }

        AllCompaniesCache.getInstance().setAllCompanies(allCompanies);

    }

    private void reloadDataWithoutSetting(){

        List<Company> followingCompanies = null;
        try {
            followingCompanies = new GetFollowingCompaniesByUserTask().execute(ParseUser.getCurrentUser()).get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        FollowingCompaniesCache.getInstance().setFollowingCompanies(followingCompanies);

        //use get since need to make sure hot companies is loaded before the next activity is started
        List<ParseIndustry> industries = null;
        try {
            industries =  new GetIndustriesTask(this).execute("").get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, Utility.getExceptionStackTrace(ex));
        }
        IndustriesCache.getInstance().setIndustries(industries);

        new GetMostRecentlyLikedPostIdsTask().execute(ParseUser.getCurrentUser());

        String industryCode = (String) ParseUser.getCurrentUser().get(ParseDatabaseColumnNames.USER_INTERESTED_INDUSTRY_CODE);
        new GetHotCompaniesByIndustryCodeTask().execute(industryCode);

        new GetHotVoicesByIndustryBeforeCreatedTimeTask(new Date()).execute(industryCode);
        new GetAllCompaniesTask(this).execute();
    }


    private void sendFragmentsUpdateBroadcast(){

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.UPDATE_HOT_VOICES_VIEW));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.UPDATE_HOT_COMPANIES_VIEW));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.UPDATE_FOLLOWING_COMPANIES_VIEW));
    }




    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }




    public void switchTabs(boolean direction) {
        if (direction) // true = move left
        {
            if (mTabHost.getCurrentTab() == 0)
                mTabHost.setCurrentTab(mTabHost.getTabWidget().getTabCount() - 1);
            else
                mTabHost.setCurrentTab(mTabHost.getCurrentTab() - 1);
        } else
        // move right
        {
            if (mTabHost.getCurrentTab() != (mTabHost.getTabWidget()
                    .getTabCount() - 1))
                mTabHost.setCurrentTab(mTabHost.getCurrentTab() + 1);
            else
                mTabHost.setCurrentTab(0);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        hideActionBarMenuItems(menu);


        int navigationMode = getActionBar().getNavigationMode();
        if(navigationMode  == ActionBar.NAVIGATION_MODE_LIST) {
            setIndustrySpinnerForActionBar();
        }


        MenuItem postActionFromTabsActivity = menu.findItem(R.id.action_make_post_tabs_activity);
        postActionFromTabsActivity.setVisible(true);


        return super.onCreateOptionsMenu(menu);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){


            case R.id.action_make_post_tabs_activity:


                HotCompaniesFragment hotCompaniesFragment = HotCompaniesFragment.newInstance(HotCompaniesFragment.SearchPurpose.POST);

                //not pop back in case user doesn't post, pop back twice if users do make a post
                pushFragmentByNewThread(handler, hotCompaniesFragment, android.R.id.tabhost, 0, HotCompaniesFragment.FRAGMENT_TAG);
                break;

            case R.id.action_create_company:

                UserCreateCompanyFragment userCreateCompanyFragment = UserCreateCompanyFragment.newInstance(ComponentSource.MainTabHostActivity);
                pushFragmentByNewThread(handler, userCreateCompanyFragment, android.R.id.tabhost, 0, UserCreateCompanyFragment.FRAGMENT_TAG);

                break;

            case R.id.action_my_posts:

                SharedPreferencesManager.getInstance(this).putBoolean(Constants.UPDATE_YOUR_POSTS_FRAGMENT, true);
                MyPostsFragment yourPostsFragment = MyPostsFragment.newInstance();
                pushFragmentByNewThread(handler, yourPostsFragment, android.R.id.tabhost, 0, MyPostsFragment.FRAGMENT_TAG);

                break;

            case R.id.action_feedback:

                Utility.trackParseEvent(AnalyticsConstants.FEEDBACK_VIEW, null, null, ComponentSource.SettingsView.getParseCode());

                showUserFeedbackDialog(null);

                break;

            case R.id.action_rules:
                RulesFragment rulesFragment = RulesFragment.newInstance(ComponentSource.MainTabHostActivity);
                pushFragmentByNewThread(handler, rulesFragment,android.R.id.tabhost, 0, RulesFragment.FRAGMENT_TAG);

                break;

            case R.id.action_logout:
                showLogoutAlertDialog();

                break;


        }

        return false;
    }

    //disable back button since haven't figure out a way to reset action bar title correctly
    @Override
    public void onBackPressed()
    {

        //don't leave the app!
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }

       String backToFragment = SharedPreferencesManager.getInstance(this).getString(Constants.BACK_TO_FRAGMENT, "");

        if(backToFragment.equals(CompanyPostsFragment.FRAGMENT_TAG) || backToFragment.equals(MyPostsFragment.FRAGMENT_TAG)){

            getActionBar().setTitle(Utility.makeActionBarTitle(SharedPreferencesManager.getInstance(this).getString(Constants.BACK_TO_TITLE, "")));

        }else{

            resetActionBar();
            showIndustryNavigatorWithoutLoading();
        }

        SharedPreferencesManager.getInstance(this).putString(Constants.BACK_TO_FRAGMENT, "");
    }



    public void showUserFeedbackDialog(final UserFeedbackReply originalReply){


        final Dialog dialog = CustomDialog.getTwoButtonsEditTextDialog(this, getString(R.string.user_feedback_alert_title));
        final EditText inputMessage = (EditText) dialog.findViewById(R.id.input_message);
        inputMessage.setVisibility(View.VISIBLE);

        TextView cancelButton = (TextView) dialog.findViewById(R.id.left_button);
        cancelButton.setText(getString(R.string.user_feedback_alert_cancel));


        TextView sendButton = (TextView) dialog.findViewById(R.id.right_button);
        sendButton.setText(getString(R.string.user_feedback_alert_send));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if((!inputMessage.getText().toString().equals(""))) {

                        userFeedbackService.createNewFeedback(inputMessage.getText().toString(), originalReply);
                        CustomDialog.showOKReminderDialog(MainTabHostActivity.this, getString(R.string.feedback_thanks_message));
                        dialog.dismiss();

                    }
                } catch (Exception e){
                    Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
                }

            }
        });

        dialog.show();
    }


    public void showLogoutAlertDialog(){


        CustomDialog.showTwoButtonsDialog(this, getString(R.string.Logout_message), null,

                getString(R.string.Yes), getString(R.string.No), new CustomDialog.CustomDialogDelegate() {
                    @Override
                    public void onComplete(int buttonIndex) {
                        //right button or positive button
                        if (buttonIndex == 0) {
                            // can't directly call SingupLoginActivty, otherwise when clicking
                            //home button and come back, SingupLoginActivty will be called again
                            Intent launchIntent = new Intent(MainTabHostActivity.this, LaunchActivity.class);
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(launchIntent);

                            finish();
                        }
                    }
                });
    }


    private void loadFeedbackReplyAndShowDialog(){

        new GetUserFeedbackReplyTask(new OnTaskCompleted<UserFeedbackReply>() {
            @Override
            public void onTaskCompleted(UserFeedbackReply result) {

                if(result!=null && result.getMessage()!=null) {
                    showFeedbackReplyDialog(result);

                }
            }
        }).execute(ParseUser.getCurrentUser());

    }


    private void showFeedbackReplyDialog(final UserFeedbackReply userFeedbackReply){

        final UserFeedbackReplyService userFeedbackReplyService = new UserFeedbackReplyServiceImpl(new UserFeedbackReplyDAOImpl());


        CustomDialog.showTwoButtonsDialog(this, getString(R.string.user_feedback_reply_title), userFeedbackReply.getMessage(),

                getString(R.string.user_feedback_reply_again), getString(R.string.OK), new CustomDialog.CustomDialogDelegate() {
                    @Override
                    public void onComplete(int buttonIndex) {
                        //right button or positive button
                        userFeedbackReply.setIsDisplayed(true);

                        if (buttonIndex == 0) {

                            showUserFeedbackDialog(userFeedbackReply);
                        }

                        else if (buttonIndex == 1) {


                        }

                        userFeedbackReplyService.saveIntoDatabase(userFeedbackReply);
                    }
                });
    }





    public void pushFragmentByNewThread(final Handler handler, final Fragment fragment, final int containerViewId, final int numberOfPopbakTimes, final String fragmentTag){
        // set navigation mode to standard - child fragment can override this when
        // they replace transaction completes
        resetActionBar();
        //use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(containerViewId, fragment, fragmentTag).addToBackStack(null);

                        int popbackCounter = numberOfPopbakTimes;
                        while(popbackCounter !=0) {
                            fm.popBackStack();
                            popbackCounter --;
                        }


                        transaction.commit();
                    }
                });
            }
        };
        new Thread(runnable).start();
    }


    public void pushFragmentByUIThread(Fragment fragment, int containerViewId, final String fragmentTag){
        // set navigation mode to standard - child fragment can override this when
        // they replace transaction completes
        resetActionBar();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
          .replace(containerViewId, fragment, fragmentTag)
          .addToBackStack(null)
          .commit();
    }

    public void createTabHostByNewThread(final Handler handler){
        //use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() { createTabHost();}
                });
            }
        };
        new Thread(runnable).start();
    }



    public void createTabHost(){

        popCurrentFragment();
        //recreate is very fast!
        FragmentTabHost mTabHost = getTabHost();
        mTabHost.clearAllTabs();
        mTabHost.addTab(mTabHost.newTabSpec("hotVoice").setIndicator("Hot Voice", null),
                HotVoicesFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("hotCompanies").setIndicator("Hot Company", null),
                HotCompaniesFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("following").setIndicator("Following", null),
                FavoriteCompaniesFragment.class, null);
    }

    public void resetActionBar(){
        ActionBar actionBar = getActionBar();

        if(actionBar !=null){
            actionBar.setSubtitle(null);
            //turn this to true otherwise no title show for action bar
            actionBar.setDisplayShowTitleEnabled(true);
            //hide the go back action bar
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

   public void resetPreActivityActionBar(){

       resetActionBar();
       showIndustryNavigatorWithoutLoading();

   }


    //hide all action bar items except for settings overflow
    public void hideActionBarMenuItems(Menu menu){

        MenuItem postActionFromTabsActivity = menu.findItem(R.id.action_make_post_tabs_activity);
        postActionFromTabsActivity.setVisible(false);

        MenuItem postActionFromCompanyPostsFragment = menu.findItem(R.id.action_make_post_company_posts_fragment);
        postActionFromCompanyPostsFragment.setVisible(false);

        MenuItem  searchBox =  menu.findItem(R.id.search_box);
        searchBox.setVisible(false);


    }


    public void popCurrentFragment(){

        resetActionBar();


        //hide any keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if(imm!=null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }


    //may need to change to customized layout by Dialog rather than alertDialog
    public void optionDialog(final Post post){
        CharSequence options[] = new CharSequence[] {getString(R.string.share),getString(R.string.flag), getString(R.string.feedback)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.option))
               .setItems(options, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       //flag the post, not remove yet
                       switch (which) {
                           case 0:
                               shareOptionsDialog(post);
                               break;
                           case 1:
                               flagOptionsDialog(post);
                               break;
                           case 2:

                               Utility.trackParseEvent(AnalyticsConstants.FEEDBACK_VIEW, AnalyticsConstants.POST_ID, post.getObjectId(), ComponentSource.PostView.getParseCode());

                               showUserFeedbackDialog(null);
                               break;
                       }

                   }
               })
                .show().setCanceledOnTouchOutside(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            // Tapped outside so we finish the activity
            this.finish();
        }
        return super.dispatchTouchEvent(ev);
    }


    //may need to change to customized layout by Dialog rather than alertDialog
    public void flagOptionsDialog(final Post post){
        CharSequence options[] = new CharSequence[] {FlagReason.NOT_RELATED.getDisplayName(), FlagReason.LOW_CREDIBILITY.getDisplayName(),FlagReason.BULLY.getDisplayName(), FlagReason.InAPPROPRIATE.getDisplayName(), FlagReason.SPAM.getDisplayName()};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.flag_message))
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        FlagReason[] flagReasons = {
                                FlagReason.NOT_RELATED,
                                FlagReason.LOW_CREDIBILITY,
                                FlagReason.BULLY,
                                FlagReason.InAPPROPRIATE,
                                FlagReason.SPAM
                        };


                        int numberFlagged = post.getNumberFlagged()+1;
                        post.setNumberFlagged(numberFlagged);
                        post.setFlagged(true);

                        new UpdatePostIntoDBTask().execute(post);
                        new CreateUserFlagPostTask((flagReasons[which])).execute(post);

                        Utility.trackParseEvent(AnalyticsConstants.DID_FLAG_POST, AnalyticsConstants.FLAG_REASON, flagReasons[which].getCode(), null);

                        CustomDialog.showOKReminderDialog(mainActivity, getString(R.string.report_thanks_message));

                }
                }).show().setCanceledOnTouchOutside(true);
    }


    //may need to change to customized layout by Dialog rather than alertDialog
    public void shareOptionsDialog(final Post post){
             final CharSequence options[] = new CharSequence[] {PostOption.EMAIL.getCode(), PostOption.SMS.getCode(),PostOption.WHATSAPP.getCode(), PostOption.OTHERS.getCode()};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Utility.trackParseEvent(AnalyticsConstants.SHARE_POST, AnalyticsConstants.POST_ID, post.getObjectId(), null);


        builder.setTitle(getString(R.string.share_message));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int numberShares = post.getNumberShares()+1;
                post.setNumberShares(numberShares);
                new UpdatePostIntoDBTask().execute(post);

                switch (which) {


                    case 0:
                        IntentLauncher.startSharePostEmailIntent(getBaseContext(), post);
                        break;
                    case 1:
                        IntentLauncher.startSharePostSMSIntent(getBaseContext(), post);
                        break;
                    case 2:
                        IntentLauncher.startSharePostWhatsappIntent(getBaseContext(), post);
                        break;
                    case 3:
                        IntentLauncher.startSharePostMessengerIntent(getBaseContext(), post);
                        break;

                }
            }
        });
        builder.show().setCanceledOnTouchOutside(true);
    }


    private void clearBackStack(){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void showKeyBoard(EditText editText){
        if(editText.requestFocus()) {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public void hideKeyboard(EditText editText){

        if(editText == null)
            return;

        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void goBackToCompanyTabHostActivity(Handler handler){
            //originally we tried to startHotVoicesFragment() but the fragment view is not called at all
            createTabHostByNewThread(handler);
            resetActionBar();
            getActionBar().setTitle(getString(R.string.empty_location));
    }


    public FragmentTabHost getTabHost(){
        return  mTabHost;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {

        //to fix onNavigationListener() is fired when activity starts and end up recursion issue
        if(industrySpinnerStartCounter!=0){
            industrySpinnerStartCounter--;

            return true;
        }

        String industryName = industrySpinnerAdapter.getItem(itemPosition);

        Utility.trackParseEvent(AnalyticsConstants.CHANGE_INDUSTRY_SPINNER, AnalyticsConstants.INDUSTRY_NAME, industryName, null);

        CustomDialog.getLoadingProgressDialog(this, getResources().getString(R.string.change_industry_loading_message)).show();

        //should clean up data to prevent cross industry data supplement, but if users shift back right away, may not see the latest created post right away
        NewCreatedPostsCache.getInstance().clear();

        updateUserIndustryInDatabase(Utility.mapIndustryNameToCode(industryName));

        startCompanyTabHostActivity();

        return true;
    }

    public void showIndustryNavigatorWithoutLoading(){

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        //activity is initializing then won't call industry spinner onNavigationItemSelected()
        industrySpinnerStartCounter =1;
    }

    public void updateUserIndustryInDatabase(String selectedIndustryCode){

        SharedPreferencesManager.getInstance(this).putString(Constants.INTERESTED_INDUSTRY_CODE, selectedIndustryCode);
        UserService userService = new UserServiceImpl(new UserDAOImpl());
        userService.updateUserIndustryCode(selectedIndustryCode);

    }

    private void subscribeNewIndustryNameNotification(String oldIndustryCode){


        String currentIndustryName = Utility.mapIndustryCodeToName(ParseUser.getCurrentUser().getString(Constants.INTERESTED_INDUSTRY_CODE));
        String oldIndustryName = Utility.mapIndustryCodeToName(oldIndustryCode);

        if(oldIndustryName!=null) {
            PushNotificationService.unsubscribeById(oldIndustryName);
        }


        if( currentIndustryName!=null) {
            PushNotificationService.subscribeById(currentIndustryName);
        }
    }

    public void startCompanyTabHostActivity(){

        String industryCode = SharedPreferencesManager.getInstance(this).getString(Constants.INTERESTED_INDUSTRY_CODE, Industry.TECHNOLOGY.getCode());

        try {
            new GetHotCompaniesByIndustryCodeTask().execute(industryCode).get();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }


        Intent companyTabHostActivity = new Intent(this, MainTabHostActivity.class);
        startActivity(companyTabHostActivity);

        finish();
    }

    private void setIndustrySpinnerForActionBar(){

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        getActionBar().setDisplayShowTitleEnabled(false);


        List<String> industryNames = IndustriesCache.getInstance().getIndustryNames();

        if(Utility.listIsEmpty(industryNames)){
            return;
        }

        // Enabling Spinner dropdown navigation
        this.industrySpinnerAdapter = new IndustrySpinnerAdapter(this, industryNames);

        // Assigning the spinner navigation
        getActionBar().setListNavigationCallbacks(industrySpinnerAdapter, this);
        getActionBar().setSelectedNavigationItem(
                Utility.getIndustryItemPosition(
                        SharedPreferencesManager.getInstance(this).getString(Constants.INTERESTED_INDUSTRY_CODE, Industry.TECHNOLOGY.getCode())
                        , IndustriesCache.getInstance().getIndustries()));
    }

    private void setIndustrySpinnerWithoutLoading() {


           //activity is initializing then won't call industry spinner onNavigationItemSelected()
           // 3 is from experiment...
           industrySpinnerStartCounter = 3;

           List<String> industryNames = SharedPreferencesManager.getInstance(this).getStringList(Constants.INDUSTRY_NAMES);
           List<String> industryCodes = SharedPreferencesManager.getInstance(this).getStringList(Constants.INDUSTRY_CODES);

           if (Utility.listIsEmpty(industryNames) || Utility.listIsEmpty(industryCodes)) {
               return;
           }


           // Enabling Spinner dropdown navigation
           this.industrySpinnerAdapter = new IndustrySpinnerAdapter(this, industryNames);

           if(getActionBar().getNavigationMode()==ActionBar.NAVIGATION_MODE_LIST) {

               getActionBar().setDisplayShowTitleEnabled(false);
               // Assigning the spinner navigation
               getActionBar().setListNavigationCallbacks(industrySpinnerAdapter, this);
               getActionBar().setSelectedNavigationItem(
                       Utility.getIndustryItemPositionByCodes(
                               SharedPreferencesManager.getInstance(this).getString(Constants.INTERESTED_INDUSTRY_CODE, Industry.TECHNOLOGY.getCode())
                               , industryCodes));
            }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        Log.i("motion", "onFling has been called!");
        final int SWIPE_MIN_DISTANCE = 120;
        final int SWIPE_MAX_OFF_PATH = 250;
        final int SWIPE_THRESHOLD_VELOCITY = 200;
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i("motion", "Right to Left");
                switchTabs(false);
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                Log.i("motion", "Left to Right");
                switchTabs(true);

            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    enum PostOption{
        EMAIL("Email"),
        SMS("SMS"),
        WHATSAPP("WhatsApp"),
        OTHERS("Others Messengers");

       String code;

       PostOption(String code){
           this.code = code;
       }

       String getCode(){
           return code;
       }
    }

    private void checkUserStatus(){

        if(Utility.isUserBlocked()){
            CustomDialog.showOKReminderDialogWithDelegate(this, getResources().getString(R.string.user_block_message), new CustomDialog.CustomDialogDelegate() {
                @Override
                public void onComplete(int buttonIndex) {
                    if(buttonIndex == 0){
                        Intent mainIntent = new Intent(mainActivity, SingupLoginActivity.class);
                        startActivity(mainIntent);
                    }
                }
            });

        }
    }

    private Company findSelectedCompanyById(String companyId){

        Company selectedCompany = FollowingCompaniesCache.getInstance().getCompanyById(companyId);
        //if following companies data is lost
        if(selectedCompany != null){

            try {
                selectedCompany = new GetCompanyByIdTask().execute(companyId).get();
            } catch (Exception e) {
                Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));

            }
        }

        return selectedCompany;
    }

    public boolean isReloadHotCompanyDataDueToRestart() {
        return reloadHotCompanyDataDueToRestart;
    }

    public void setReloadHotCompanyDataDueToRestart(boolean isReentered) {
        this.reloadHotCompanyDataDueToRestart = isReentered;
    }

    //    //for company list has new posts
//    private void updateCompanyCheckTimeMap(){
//
//        //cache it at local
//        Map<String, Date> map = SharedPreferencesManager.getInstance(this).getStringDateMap(Constants.COMPANY_CHECKOUT_TIME_MAP);
//        if(map == null){
//            map = new HashMap<String, Date>();
//         }
//
//        List<Company> allCompanies = AllCompaniesCache.getInstance().getAllCompanies();
//        if(!Utility.listIsEmpty(allCompanies)){
//
//            for(Company company: allCompanies){
//
//                Date checkoutTime = map.get(company.getObjectId());
//                if(checkoutTime == null){
//                    map.put(company.getObjectId(), new Date(0));
//                }
//
//            }
//        }
//
//        SharedPreferencesManager.getInstance(this).putStringDateMap(Constants.COMPANY_CHECKOUT_TIME_MAP, map);
//    }
}

