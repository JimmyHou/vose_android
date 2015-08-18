package com.vose.fragment.action;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.voice.app.R;
import com.parse.ParseUser;
import com.vose.AsyncTask.GetCommentsByParentPostAfterCreatedTimeTask;
import com.vose.AsyncTask.GetUserMadeAndLikedPostsTask;
import com.vose.AsyncTask.PostIsLikedByUserTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.adapter.PostListAdapter;
import com.vose.data.model.post.Post;
import com.vose.data.model.util.ComponentSource;
import com.vose.fragment.postcomment.PostCommentsFragment;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 12/24/14.
 */
public class MyPostsFragment extends ListFragment {

//----------
// Enum and constants
//-----------
private static final String LOG_TAG = "MyPostsFragment";
public static final String FRAGMENT_TAG = "FRAGMENT_MY_POSTS";

//-----------
// Member vars
//-------------
private PostListAdapter adapter = null;
private View rootView;
private TextView userNoPostsView;
private View progressSection;
private Handler handler = null;
private List<Post> yourPosts;


    public static MyPostsFragment newInstance(){


        return new MyPostsFragment();
    }


    //not loading again
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_my_posts, container, false);

        userNoPostsView = (TextView) rootView.findViewById(R.id.user_no_post);
        progressSection =  rootView.findViewById(R.id.progressSection);

        setActionBar();

        handler = new Handler();

        setBackPressControlVariables();

        return rootView;
    }

    private void setBackPressControlVariables(){

            SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_FRAGMENT, MyPostsFragment.FRAGMENT_TAG);
            SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_TITLE, getString(R.string.my_posts_title));
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // pass the company name to the next activity, cast to String temporarily, should actually pass in the Company entity!
        final Post selectedPost = (Post) l.getItemAtPosition(position);

        //need to pass in a very old time
        final GetCommentsByParentPostAfterCreatedTimeTask getCommentsByParentPostAfterCreatedTimeTask = new GetCommentsByParentPostAfterCreatedTimeTask(new Date(0));
        getCommentsByParentPostAfterCreatedTimeTask.execute(selectedPost);

        final PostIsLikedByUserTask postIsLikedByUserTask = new PostIsLikedByUserTask();
        postIsLikedByUserTask.execute(selectedPost);

        PostCommentsFragment postCommentListFragment = PostCommentsFragment.newInstance(selectedPost, v, ComponentSource.MyPostsFragment, getCommentsByParentPostAfterCreatedTimeTask, postIsLikedByUserTask);
        ((MainTabHostActivity)getActivity()).pushFragmentByNewThread(handler, postCommentListFragment, android.R.id.tabhost, 0, PostCommentsFragment.FRAGMENT_TAG);

    }


    @Override
    public  void onStart(){
        super.onStart();

        Utility.trackParseEvent(AnalyticsConstants.MY_POST_VIEW, AnalyticsConstants.USER_ID, ParseUser.getCurrentUser().getObjectId(), null);


        if(SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.UPDATE_YOUR_POSTS_FRAGMENT)) {
            runYourPostsUpdateTask();
        }else{

            progressSection.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        ((MainTabHostActivity)getActivity()).hideActionBarMenuItems(menu);

        MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
        overFlowMenu.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MainTabHostActivity mainActivity = (MainTabHostActivity) getActivity();

        switch (item.getItemId()){
            case android.R.id.home:

                getActivity().getSupportFragmentManager().popBackStack();
                SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_FRAGMENT, "");

                //hide go back button
                ((MainTabHostActivity)getActivity()).resetPreActivityActionBar();

                mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);


                return false;
        }

        return false;
    }

    private void setActionBar(){

        setHasOptionsMenu(true);

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(getString(R.string.my_posts_title));

    }

    public void runYourPostsUpdateTask(){

        // use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        try {

                            //yourPosts = new GetPostsByUserTask().execute(ParseUser.getCurrentUser()).get();
                            yourPosts = new GetUserMadeAndLikedPostsTask().execute(ParseUser.getCurrentUser()).get();
                            updateView(yourPosts);
                        } catch (Exception e) {
                            Log.e("Failed to query user's posts by user id" + ParseUser.getCurrentUser(), e.toString());
                        }

                    }
                });
            }
        };
        new Thread(runnable).start();
    }


    private void updateView(List<Post> yourPosts){

        progressSection.setVisibility(View.GONE);

        if(Utility.listIsEmpty(yourPosts)){
            userNoPostsView.setText(R.string.user_no_post_message);
            userNoPostsView.setVisibility(View.VISIBLE);
        }else {


            //use the same adapter of HotVoice since hot voice item and your post item looks exactly the same
            adapter = new PostListAdapter((MainTabHostActivity) getActivity(), android.R.layout.simple_list_item_multiple_choice, yourPosts, ComponentSource.MyPostsFragment);
            setListAdapter(adapter);

        }
    }
}
