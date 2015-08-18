package com.vose.fragment.makepost;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.AsyncTask.CreateUserFollowCompanyTask;
import com.vose.AsyncTask.GetCompanyByIdTask;
import com.vose.AsyncTask.GetPostsByCompanyBeforeCreatedTimeTask;
import com.vose.AsyncTask.UpdateCompanyIntoDBTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.cache.NewCreatedPostsCache;
import com.vose.core.data.dao.post.PostDAOImpl;
import com.vose.core.data.service.post.PostServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.post.Post;
import com.vose.data.model.util.ComponentSource;
import com.vose.data.model.util.UserStatus;
import com.vose.fragment.postcomment.CompanyPostsFragment;
import com.vose.helper.PostCounter;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.CustomDialog;
import com.vose.util.ParseDatabaseColumnNames;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.Date;

/**
 * Created by jimmyhou on 2014/11/20.
 */
public class MakePostFragment extends Fragment {

    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "MakeAPostFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_MAKE_A_POST";

    private Company mSelectedCompany;
    private Handler handler;
    private EditText fPostContents;
    private Post mNewCreatedPost;
    private ComponentSource mComponentSource;
    private String mPostContents;
    private GetPostsByCompanyBeforeCreatedTimeTask getPostsByCompanyBeforeCreatedTimeTask;
    private final int NUMBER_CHARACTERS_LIMITS = 400;


    private final String STATE = "state";
    private final String SELECTED_COMPANY_ID = "selectedCompanyId";
    private final String COMPONENT_SOURCE = "component_source";
    private final String POST_CONTENTS = "post_contents";


    public MakePostFragment newInstance(Company selectedCompany, ComponentSource componentSource){

        MakePostFragment makeAPostFragment = new MakePostFragment();
        makeAPostFragment.mSelectedCompany = selectedCompany;
        makeAPostFragment.mComponentSource = componentSource;

        return  makeAPostFragment;
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
                Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));

            }

            mComponentSource = (ComponentSource) state.get(COMPONENT_SOURCE);

            mPostContents = state.getString(POST_CONTENTS);
        }




        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

       View fRootView = inflater.inflate(R.layout.fragment_make_post, container, false);

       fPostContents = (EditText) fRootView.findViewById(R.id.post_content);

       fPostContents.setText(mPostContents);

       ((MainTabHostActivity)getActivity()).showKeyBoard(fPostContents);

       handler = new Handler();

       //set actionBar
       setActionBar();

        setBackPressControlVariables();

       return fRootView;
    }

    private void setActionBar(){

        setHasOptionsMenu(true);

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(Utility.makeActionBarTitle(mSelectedCompany.getName()));

    }

    private void setBackPressControlVariables(){

        SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_FRAGMENT, CompanyPostsFragment.FRAGMENT_TAG);


        if(mComponentSource == ComponentSource.CompanyPostsFragment) {

            SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_TITLE, mSelectedCompany.getName());

        }else  if(mComponentSource == ComponentSource.SearchFragment){

            SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_TITLE, getString(R.string.select_company_to_post_title));
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        ((MainTabHostActivity)getActivity()).hideActionBarMenuItems(menu);

        MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
        overFlowMenu.setVisible(false);

        MenuItem post = menu.findItem(R.id.post);
        post.setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.post:
                //prevent empty typing
                if(fPostContents.getText().toString().trim().equals("")) {
                    return false;
                }

                if(!isEligibleToPost()){
                    return false;
                }

                //remove space
                String postContents = fPostContents.getText().toString();
                postContents =  postContents.replaceAll("[\r\n]+", "\n");

                if(postContents.length() <= NUMBER_CHARACTERS_LIMITS){
                    makeNewPost(postContents);
                    startCompanyPostsFragment();

                }else{

                    CustomDialog.showOKReminderDialog(getActivity(),getResources().getString(R.string.post_too_long_message_head)+
                            postContents.length()+
                            getResources().getString(R.string.post_too_long_message_end));
                }

                return  false;

            case android.R.id.home:

                SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_FRAGMENT, "");

                ((MainTabHostActivity)getActivity()).popCurrentFragment();

                return  false;
        }

        return false;
    }

    private void makeNewPost(String postContents){


        Utility.trackParseEvent(AnalyticsConstants.MAKE_POST_VIEW,AnalyticsConstants.COMPANY_NAME, mSelectedCompany.getName(), mComponentSource .getParseCode());

        //follow this company if it's not following yet
        if(!FollowingCompaniesCache.getInstance().alreadyFollowThisCompany(mSelectedCompany)){

            new CreateUserFollowCompanyTask().execute(mSelectedCompany);
            //the latest followed company is at the top
            FollowingCompaniesCache.getInstance().getFollowingCompanies().add(0, mSelectedCompany);
        }

        //in order to fully control newCreatedPost must show up, we load the updated post before the post is created in DB!
        getPostsByCompanyBeforeCreatedTimeTask = new GetPostsByCompanyBeforeCreatedTimeTask(new Date());
        getPostsByCompanyBeforeCreatedTimeTask.execute(mSelectedCompany);

        // CreateNewPostTask is slow, creating entity and saveIntoDB separately is faster
//        try {
//            mNewCreatedPost =   new CreateNewPostTask(mSelectedCompany).execute(postContents).get();
//        } catch (Exception e) {
//            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
//        }

        mNewCreatedPost = new PostServiceImpl(new PostDAOImpl()).createNewPost(postContents, mSelectedCompany);
        NewCreatedPostsCache.getInstance().getNewCreatedPosts().add(0, mNewCreatedPost);

        mSelectedCompany.setNumberPosts(mSelectedCompany.getNumberPosts()+1);
        mSelectedCompany.setPostTime(new Date());
        new UpdateCompanyIntoDBTask().execute(mSelectedCompany);
        ((MainTabHostActivity)getActivity()).hideKeyboard(fPostContents);
   }


    private void startCompanyPostsFragment(){

        CompanyPostsFragment companyPostsFragment =  CompanyPostsFragment.newInstance(mSelectedCompany, mComponentSource, getPostsByCompanyBeforeCreatedTimeTask, mNewCreatedPost);

        if(mComponentSource == ComponentSource.SearchFragment) {

            ((MainTabHostActivity) getActivity()).pushFragmentByNewThread(handler, companyPostsFragment, android.R.id.tabhost, 1, MakePostFragment.FRAGMENT_TAG);

        }else  if(mComponentSource == ComponentSource.CompanyPostsFragment){

            ((MainTabHostActivity) getActivity()).pushFragmentByNewThread(handler, companyPostsFragment, android.R.id.tabhost, 2,  MakePostFragment.FRAGMENT_TAG);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Bundle bundle = new Bundle();

        //can't save Company since it's a ParseObject, when retrieve it, all the member variables will be lost
        bundle.putString(SELECTED_COMPANY_ID, mSelectedCompany.getObjectId());

        bundle.putSerializable(COMPONENT_SOURCE,mComponentSource);

        bundle.putString(POST_CONTENTS, fPostContents.getText().toString());

        savedInstanceState.putBundle(STATE,bundle);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void startCompanyTabHostActivity(){

        Intent companyTabHostActivity = new Intent(getActivity(), MainTabHostActivity.class);
        startActivity(companyTabHostActivity);

        getActivity().finish();
    }

    private boolean isEligibleToPost() {

        int postLimit = PostCounter.getInstance().getPostsLimit();
        int numberPosts = PostCounter.getInstance().trackTimeAndPosts();

        if (numberPosts == postLimit - 1) {

            CustomDialog.showOKReminderDialog(getActivity(), getResources().getString(R.string.post_warning_message));

            ParseUser.getCurrentUser().put(ParseDatabaseColumnNames.USER_STATUS, UserStatus.REVIEW.getCode());

            return true;

        } else if (numberPosts >= postLimit) {
            //block
            String message1 = getResources().getString(R.string.post_block_message_head);
            String message2 = getResources().getString(R.string.post_block_message_end);
            long blockTimeMins = PostCounter.getInstance().getBlockTime()/(60*1000);
            String message = message1+String.valueOf(blockTimeMins)+message2;

            CustomDialog.showOKReminderDialog(getActivity(), message);
            return false;
        } else {

            return true;
        }

    }


}
