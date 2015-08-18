package com.vose.fragment.postcomment;

import android.app.ActionBar;
import android.graphics.Typeface;
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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.voice.app.R;
import com.vose.AsyncTask.CreateNewCommentTask;
import com.vose.AsyncTask.GetCommentsByParentPostAfterCreatedTimeTask;
import com.vose.AsyncTask.PostIsLikedByUserTask;
import com.vose.AsyncTask.UpdatePostAndAddPostLikeTask;
import com.vose.AsyncTask.UpdatePostAndDeletePostLikeTask;
import com.vose.AsyncTask.UpdatePostIntoDBTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.adapter.CommentListAdapter;
import com.vose.cache.AlreadyLikedPostIdsCache;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.Post;
import com.vose.data.model.util.ComponentSource;
import com.vose.helper.PrettyTimeHelper;
import com.vose.util.AnalyticsConstants;
import com.vose.util.Constants;
import com.vose.util.LinkTextView;
import com.vose.util.SharedPreferencesManager;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/22.
 */
public class PostCommentsFragment extends ListFragment {
    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "PostCommentsFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_POST_COMMENTS";

    //-----------
    // Member vars
    //-------------

    private Post mParentPost = null;
    private ListView fCommentListView = null;
    private int currentNumberComments;
    private int numberLikes;
    private CommentListAdapter mAdapter = null;
    private List<Comment> mPostComments = null;
    private Comment mLastComment = null;
    private boolean isAdapterCreated = false;
    private boolean reachedTheEndOfComments = false;
    private ComponentSource mComponentSource = null;
    private Handler handler = null;

    private LinearLayout fPostCardFrame = null;
    private LinearLayout fPostMessageSection = null;
    private EditText fNewCommentView = null;
    private View fParentPostViewPre = null;
    private View fRootView = null;
    private LinkTextView fPostTextView = null;
    private TextView fCompanyNameView = null;
    private TextView fNumberCommentsTextView = null;
    private TextView fParentPostNumberLikesView = null;
    private ImageView fParentPostLikeButton = null;
    private ImageView fParentPostOptionView = null;
    private TextView fRelativeTimeView = null;
    private GetCommentsByParentPostAfterCreatedTimeTask getCommentsByParentPostAfterCreatedTimeTask= null;
    private TextView fNoCommentTextView = null;
    private View fProgressSection;

    private boolean restartMainActivity = false;




    //--------------------------
    //
    // fParentPostViewPre is the previous postView, put it here to update the post in previous fragment! We can't just pass the parentPostView and update it
    // we need to inflate a new post view in the fragment and update both the new one and old one!
    // otherwise the post will have overlap or not updated after going back to previous fragment!
    //--------------------------


    //Android doesn't like fragment constructor with parameters, so use new instance instead, as factor pattern
    public static PostCommentsFragment newInstance(Post parentPost, View parentPostViewPre, ComponentSource componentSource, GetCommentsByParentPostAfterCreatedTimeTask getCommentsByParentPostAfterCreatedTimeTask, PostIsLikedByUserTask postIsLikedByUserTask){
        PostCommentsFragment postCommentListFragment = new PostCommentsFragment();

        //must use this way to set up parameters, this.a = a won't be persistent
        postCommentListFragment.mParentPost = parentPost;
        postCommentListFragment.mComponentSource = componentSource;
        postCommentListFragment.getCommentsByParentPostAfterCreatedTimeTask= getCommentsByParentPostAfterCreatedTimeTask;
        postCommentListFragment.fParentPostViewPre = parentPostViewPre;


        return postCommentListFragment;
    }



    //recreate the state
    @Override
    public void onCreate(Bundle savedInstanceState){

        if(savedInstanceState!=null){

            restartMainActivity = true;

        }


        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        handler = new Handler();

        if(restartMainActivity){

            return null;
        }

        fRootView = inflater.inflate(R.layout.fragment_post_comments, container, false);
        fProgressSection = fRootView.findViewById(R.id.progressSection);



        setBackPressControlVariables();

        setupParentPostAndCommentListView(mParentPost);
        setUpComponentsListener();

        //hide the action bar, before rendering view!

        if(mComponentSource == ComponentSource.HotVoicesFragment){
            getActivity().getActionBar().setTitle(mParentPost.getCompanyName());

            int actionBarTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView abTitle = (TextView) getActivity().findViewById(actionBarTitleId);
            abTitle.setTextColor(getActivity().getBaseContext().getResources().getColor(R.color.white));
        }


        setActionBar();


        return fRootView;
    }

    private void setBackPressControlVariables(){

        if(mComponentSource == ComponentSource.CompanyPostsFragment ) {

            SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_FRAGMENT, CompanyPostsFragment.FRAGMENT_TAG);


        }

        else {
            SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_FRAGMENT, "");
        }

        SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_TITLE, mParentPost.getCompanyName());

    }


    private void setupParentPostAndCommentListView(final Post parentPost){

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/copyfonts.com_gotham-light.ttf");


        fCommentListView = (ListView) fRootView.findViewById(android.R.id.list);
        fNoCommentTextView = (TextView) fRootView.findViewById(R.id.post_comments_no_comment);


        //just use the hot_voice_item layout but bigger min height and make post frame transparent
        View fParentPostView = View.inflate(getActivity().getBaseContext(),R.layout.hot_voice_item,null);
        fParentPostView.setBackground(getResources().getDrawable(R.drawable.background_color));

        fPostCardFrame = (LinearLayout) fParentPostView.findViewById(R.id.post_card_frame);
        fPostCardFrame.setBackgroundResource(android.R.color.transparent);

        fPostMessageSection = (LinearLayout) fParentPostView.findViewById(R.id.post_message_section);
        fPostMessageSection.setMinimumHeight(350);


        fPostTextView = (LinkTextView) fParentPostView.findViewById(R.id.post_message);
        fPostTextView.setMovementMethod(LinkTextView.LinkTextViewMovementMethod.getInstance());
        fPostTextView.setText(mParentPost.getMessage());
        fPostTextView.setTypeface(tf);


        fPostTextView.setMovementMethod(LinkTextView.LinkTextViewMovementMethod.getInstance());


        fParentPostOptionView = (ImageView) fParentPostView.findViewById(R.id.post_option_image);
        fParentPostOptionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainTabHostActivity mainActivity = (MainTabHostActivity) getActivity();
                mainActivity.optionDialog(parentPost);
            }
        });

        fCompanyNameView = (TextView) fParentPostView.findViewById(R.id.company_name);
        fCompanyNameView.setVisibility(View.GONE);

        fParentPostLikeButton = (ImageView) fParentPostView.findViewById(R.id.like_button_image);

        //must use parentView.findViewById() otherwise will return null!
        currentNumberComments = mParentPost.getNumberComments();
        fNumberCommentsTextView = (TextView) fParentPostView.findViewById(R.id.number_comments);
        fNumberCommentsTextView.setText(String.valueOf(currentNumberComments));


        fParentPostNumberLikesView = (TextView) fParentPostView.findViewById(R.id.number_likes);
        numberLikes = mParentPost.getNumberLikes();
        fParentPostNumberLikesView.setText((String.valueOf(numberLikes)));

        fRelativeTimeView = (TextView) fParentPostView.findViewById(R.id.post_relative_time);
        fRelativeTimeView.setText(PrettyTimeHelper.convertToSimplePrettyTime(mParentPost.getCreatedAt()));

        //use addHeaderView to connect a view and a list view and scroll together!
        fCommentListView.addHeaderView(fParentPostView);
    }

    private void setUpComponentsListener(){
        //reset number likes view everytime unliked
        fParentPostLikeButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_like_before));

        fParentPostLikeButton.setTag(Boolean.FALSE);
        if(AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().contains(mParentPost.getObjectId())){
            fParentPostLikeButton.setTag(Boolean.TRUE);
            fParentPostLikeButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_like_after));
        }


        //click post like
        fParentPostLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) fParentPostLikeButton.getTag() == Boolean.FALSE) {
                    fParentPostLikeButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_like_after));

                    int currentNumberLikes = mParentPost.getNumberLikes() + 1;
                    mParentPost.setNumberLikes(currentNumberLikes);
                    fParentPostNumberLikesView.setText(String.valueOf(currentNumberLikes));
                    fParentPostLikeButton.setTag(Boolean.TRUE);
                    //update like button UI
                    AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().add(mParentPost.getObjectId());

                    new UpdatePostAndAddPostLikeTask().execute(mParentPost);


                    if(fParentPostViewPre!=null) {
                        TextView fParentPostNumberLikesViewPre = (TextView) fParentPostViewPre.findViewById(R.id.number_likes);
                        fParentPostNumberLikesViewPre.setText(String.valueOf(currentNumberLikes));

                        ImageView fParentPostLikeButtonPre = (ImageView) fParentPostViewPre.findViewById(R.id.like_button_image);
                        fParentPostLikeButtonPre.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_like_after));
                        fParentPostLikeButtonPre.setTag(Boolean.TRUE);
                    }

                }else{
//                    Utility.showShortToastMessage(getActivity(), getActivity().getResources().getString(R.string.already_liked_post));
                    fParentPostLikeButton.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_like_before));

                    int currentNumberLikes = mParentPost.getNumberLikes() - 1;
                    mParentPost.setNumberLikes(currentNumberLikes);
                    fParentPostNumberLikesView.setText(String.valueOf(currentNumberLikes));
                    fParentPostLikeButton.setTag(Boolean.FALSE);
                    //update like button UI
                    AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().remove(mParentPost.getObjectId());

                    new UpdatePostAndDeletePostLikeTask().execute(mParentPost);

                    if(fParentPostViewPre!=null) {
                        TextView fParentPostNumberLikesViewPre = (TextView) fParentPostViewPre.findViewById(R.id.number_likes);
                        fParentPostNumberLikesViewPre.setText(String.valueOf(currentNumberLikes));

                        ImageView fParentPostLikeButtonPre = (ImageView) fParentPostViewPre.findViewById(R.id.like_button_image);
                        fParentPostLikeButtonPre.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_like_before));
                        fParentPostLikeButtonPre.setTag(Boolean.FALSE);
                    }

                }
            }
        });



        fNewCommentView = (EditText) fRootView.findViewById(R.id.new_comment);

        //leave a comment but doesn't allow empty comment
        //setOnClickListener at comment will also refresh the post number of likes and comments when it goes back to previous page!!
        fRootView.findViewById(R.id.leave_comment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fNoCommentTextView.setVisibility(View.GONE);
                if(fNewCommentView.getText().toString().equals(""))
                    return;

                try {
                    mPostComments.add(new CreateNewCommentTask(mParentPost).execute(fNewCommentView.getText().toString()).get());
                } catch (Exception e) {
                    Log.e("failed to create new comment into DB for user: "+ParseUser.getCurrentUser().getObjectId(),e.toString());
                }
                fNewCommentView.setText("");
                fPostTextView.setText(mParentPost.getMessage());

                int currentNumberComments = mParentPost.getNumberComments()+1;
                mParentPost.setNumberComments(currentNumberComments);
                fNumberCommentsTextView.setText(String.valueOf(currentNumberComments));

                new UpdatePostIntoDBTask().execute(mParentPost);
                updateListView(mPostComments);

                ((MainTabHostActivity)getActivity()).hideKeyboard(fNewCommentView);
            }
        });
        // scrolling in list view
        if(!reachedTheEndOfComments && mPostComments !=null && mPostComments.size()!=0) {
            fCommentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //the post also counts for visibleItemCount
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    // first time to reached end of list, load more data
                    if (isAdapterCreated && lastItem == totalItemCount && !reachedTheEndOfComments) {
                        mLastComment = mPostComments.get(mPostComments.size() - 1);
                        try {
                            List<Comment> newLoadedComments = new GetCommentsByParentPostAfterCreatedTimeTask(mLastComment.getCreatedAt()).execute(mParentPost).get();
                            //check if this is the last comment
                            if (mLastComment.getObjectId().equals(newLoadedComments.get(newLoadedComments.size() - 1).getObjectId())) {
                                reachedTheEndOfComments = true;
                            } else {
                                mPostComments.addAll(newLoadedComments);
                                updateListView(mPostComments);
                                mLastComment = newLoadedComments.get(newLoadedComments.size() - 1);
                            }
                        } catch (Exception e) {
                            Log.e("Failed to scroll new comments from DB by user " + ParseUser.getCurrentUser() + " for post " + mParentPost.getObjectId(), e.toString());
                        }
                    }
                }
            });
        }
    }


    private void setLoadingListView(){

        setListAdapter(null);
    }

    private void updateListView(List<Comment> postComments){

        fProgressSection.setVisibility(View.GONE);

        if(Utility.listIsEmpty(postComments)){

            fNoCommentTextView.setVisibility(View.VISIBLE);

            setListAdapter(null);

        }else{
            fNoCommentTextView.setVisibility(View.GONE);
            //* this will affect scroll down, but just leave it there now otherwise
            //  mAdapter.notifyDataSetChanged(); won't update view when re-enter the app

            isAdapterCreated = true;
            mAdapter = new CommentListAdapter(getActivity().getBaseContext(), android.R.layout.simple_list_item_multiple_choice, mParentPost, postComments);
            setListAdapter(mAdapter);
         }
    }

    @Override
    public void onStart(){

        super.onStart();



        if(!restartMainActivity) {
            Utility.trackParseEvent(AnalyticsConstants.POST_COMMENTS_VIEW, AnalyticsConstants.POST_ID, mParentPost.getObjectId(), null);
            //put on parentPost first and run task to load comments
            setLoadingListView();
            runCommentsUpdateTask();
        }else{

            ((MainTabHostActivity)getActivity()).startCompanyTabHostActivity();
        }
    }



    public void runCommentsUpdateTask() {

        // use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        try {

                            mPostComments = getCommentsByParentPostAfterCreatedTimeTask.get();

                            updateListView(mPostComments);
                        } catch (Exception e) {
                            Log.e("Failed to query user's posts by user id" + ParseUser.getCurrentUser(), e.toString());
                        }

                    }
                });
            }
        };
        new Thread(runnable).start();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        ((MainTabHostActivity)getActivity()).hideActionBarMenuItems(menu);

        if(mComponentSource == ComponentSource.MyPostsFragment){

            MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
            overFlowMenu.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }


    //"Return false to allow normal menu processing to proceed, true to consume it here."
    //so the onOptionsItemSelected in previous fragment will be called first, if it returns false, then the onOptionsItemSelected at this fragrment will be called
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       MainTabHostActivity mainActivity = ((MainTabHostActivity) getActivity());

        switch (item.getItemId()){
           case android.R.id.home:

               SharedPreferencesManager.getInstance(getActivity()).putString(Constants.BACK_TO_FRAGMENT, "");


               if(mComponentSource == ComponentSource.HotVoicesFragment) {

                   mainActivity.popCurrentFragment();
                   mainActivity.showIndustryNavigatorWithoutLoading();

                   return false;
               }
               else if(mComponentSource == ComponentSource.CompanyPostsFragment) {

                   mainActivity.popCurrentFragment();

                   getActivity().getActionBar().setTitle(Utility.makeActionBarTitle(mParentPost.getCompanyName()));

                   return false;
               }else if(mComponentSource == ComponentSource.MyPostsFragment){

                   SharedPreferencesManager.getInstance(getActivity()).putBoolean(Constants.UPDATE_YOUR_POSTS_FRAGMENT, false);

                   mainActivity.popCurrentFragment();
               }
       }
       return false;
    }


    private void setActionBar(){

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        setHasOptionsMenu(true);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(Utility.makeActionBarTitle(mParentPost.getCompanyName()));

    }

}
