package com.vose.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.voice.app.R;
import com.vose.AsyncTask.UpdatePostAndAddPostLikeTask;
import com.vose.AsyncTask.UpdatePostAndDeletePostLikeTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.cache.AlreadyLikedPostIdsCache;
import com.vose.data.model.post.Post;
import com.vose.data.model.util.ComponentSource;
import com.vose.helper.PrettyTimeHelper;
import com.vose.util.LinkTextView;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/6.
 */

//Originally was HotVoicesAdapter but now changed to PostListAdapter to share with CompanyPostsFragment
public class PostListAdapter extends ArrayAdapter<Post> {

    //----------
    // Enum and constants
    //-----------
    private static final String LOG_TAG = "PostListAdapter";

    private MainTabHostActivity mainActivity;
    private ComponentSource componentSource;
    private Typeface typeface;


    public PostListAdapter(MainTabHostActivity mainActivity, int layoutResourceId, List<Post> posts,  ComponentSource componentSource) {
        super(mainActivity.getBaseContext(), layoutResourceId, posts);
        this.mainActivity = mainActivity;
        this.componentSource = componentSource;

        typeface = Typeface.createFromAsset(mainActivity.getAssets(),
                "fonts/copyfonts.com_gotham-light.ttf");

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LinkTextView postMessageView = null;
        TextView numberCommentsView = null;
        TextView companyNameView =  null;
        TextView numberLikesView = null;
        Button optionButtonView = null;
        TextView relativeTimeView = null;
        Button likeButtonView = null;
        ImageView likeButtonImageView = null;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mainActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hot_voice_item, parent, false);
            if (convertView != null) {
                //set up ViewHolder
                HotVoiceViewHolderItem holderItem = new HotVoiceViewHolderItem();
                holderItem.companyNameView = companyNameView = (TextView) convertView.findViewById(R.id.company_name);
                holderItem.postMessageView = postMessageView = (LinkTextView) convertView.findViewById(R.id.post_message);
                holderItem.numberCommentsView = numberCommentsView = (TextView) convertView.findViewById(R.id.number_comments);
                holderItem.numberLikesView = numberLikesView = (TextView) convertView.findViewById(R.id.number_likes);
                holderItem.likeButtonView = likeButtonView = (Button) convertView.findViewById(R.id.like_button);
                holderItem.likeButtonImageView =  likeButtonImageView = (ImageView) convertView.findViewById(R.id.like_button_image);
                holderItem.optionButtonView = optionButtonView = (Button) convertView.findViewById(R.id.post_option_button);
                holderItem.relativeTimeView = relativeTimeView= (TextView)convertView.findViewById(R.id.post_relative_time);

                // Store the holder with the view.
                convertView.setTag(holderItem);

            }
        }else{
            //we just avoid calling findViewById() on resource everytime, passing the same convertView to get this view holder everytime
            HotVoiceViewHolderItem holderItem = (HotVoiceViewHolderItem) convertView.getTag();
            companyNameView = holderItem.companyNameView;
            postMessageView = holderItem.postMessageView;
            numberCommentsView = holderItem.numberCommentsView;
            numberLikesView = holderItem.numberLikesView;
            likeButtonView = holderItem.likeButtonView;
            likeButtonImageView = holderItem.likeButtonImageView;
            optionButtonView=holderItem.optionButtonView;
            relativeTimeView = holderItem.relativeTimeView;

        }

        //set up view component here
        if(convertView!=null){

            postMessageView.setTypeface(typeface);

            final Post post = this.getItem(position);
            setViewsForItem(post,postMessageView, companyNameView, optionButtonView, numberCommentsView, numberLikesView,likeButtonView, likeButtonImageView, relativeTimeView);

            //this is necessary to trigger linkTextView other it's the link is unclickable
            postMessageView.setMovementMethod(LinkTextView.LinkTextViewMovementMethod.getInstance());
        }

        return convertView;
    }


    private void setViewsForItem(final Post post, TextView postMessageView, TextView companyNameView, Button optionButtonView, TextView numberCommentsView, final TextView numberLikesView, final Button likeButtonView, final ImageView likeButtonImageView, final TextView relativeTimeView){
        postMessageView.setText(post.getMessage());
        numberCommentsView.setText(String.valueOf(post.getNumberComments()));

        if(componentSource == ComponentSource.CompanyPostsFragment) {

            companyNameView.setVisibility(View.GONE);

        }else{
            companyNameView.setText(post.getCompanyName());
        }
        relativeTimeView.setText(PrettyTimeHelper.convertToSimplePrettyTime(post.getCreatedAt()));


//        companyNameView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //need to set position for going back to hot voices list
//               // SharedPreferencesManager.getInstance(mainAcivity).setPreferenceInteger(Constants.HOT_VOICES_ITEM_CLICK_POSITION, position);
//
//                Company selectedCompany = null;
//                try {
//                    //post.getCompany() will only get the objectId of the company so also need to store company name in post table
//                    //By default, when fetching an object, related ParseObjects are not fetched. These objects' values cannot be retrieved until they have been fetched like so:
//                    selectedCompany = post.getFetchedCompany();
//                } catch (ParseException e) {
//                    Log.e(LOG_TAG, "failed to fetch selected company from parse");
//                }
//                // Set up a progress dialog
//                fLoadCompanyPostsProgressDialog = new ProgressDialog(mainAcivity);
//                fLoadCompanyPostsProgressDialog.setMessage("Checking voices in " + selectedCompany.getName());
//                fLoadCompanyPostsProgressDialog.show();
//
//
//                //try to use fragment instead of activity
//                final GetPostsByCompanyBeforeCreatedTimeTask getPostsByCompanyBeforeCreatedTimeTask = new GetPostsByCompanyBeforeCreatedTimeTask(new Date());
//                getPostsByCompanyBeforeCreatedTimeTask.execute(selectedCompany);
//
//
//                Handler handler = new Handler();
//                CompanyPostsFragment companyPostListFragment = new CompanyPostsFragment().newInstance(selectedCompany, ComponentSource.HotVoicesFragment, getPostsByCompanyBeforeCreatedTimeTask, null, fLoadCompanyPostsProgressDialog);
//
//                mainAcivity.pushFragmentByNewThread(handler, companyPostListFragment, android.R.id.tabhost, 0, HotVoicesFragment.FRAGMENT_TAG);
//
//            }
//        });


                optionButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.optionDialog(post);
            }
        });


        //set up number of likes view and its listener
        numberLikesView.setText(String.valueOf(post.getNumberLikes()));
        //reset like clicked each time, since the numberLivesView is from viewHolder
        likeButtonImageView.setBackground(mainActivity.getResources().getDrawable(R.drawable.ic_like_before));
         //means not clicked yet by tag
        likeButtonView.setTag(Boolean.FALSE);
        if(AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().contains(post.getObjectId())){
            likeButtonView.setTag(Boolean.TRUE);
            likeButtonImageView.setBackground(mainActivity.getResources().getDrawable(R.drawable.ic_like_after));
        }


        likeButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) likeButtonView.getTag() == Boolean.FALSE) {
                    likeButtonImageView.setBackground(mainActivity.getResources().getDrawable(R.drawable.ic_like_after));

                    int currentNumberLikes = post.getNumberLikes() + 1;
                    post.setNumberLikes(currentNumberLikes);
                    numberLikesView.setText(String.valueOf(currentNumberLikes));
                    //already like it
                    likeButtonView.setTag(Boolean.TRUE);
                    AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().add(post.getObjectId());

                    new UpdatePostAndAddPostLikeTask().execute(post);

                }else{

                    //Utility.showShortToastMessage(mainActivity, mainActivity.getResources().getString(R.string.already_liked_post));
                    //allow user to unlike
                    likeButtonImageView.setBackground(mainActivity.getResources().getDrawable(R.drawable.ic_like_before));

                    int currentNumberLikes = post.getNumberLikes() - 1;
                    post.setNumberLikes(currentNumberLikes);
                    numberLikesView.setText(String.valueOf(currentNumberLikes));
                    //already like it
                    likeButtonView.setTag(Boolean.FALSE);
                    AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().remove(post.getObjectId());

                    new UpdatePostAndDeletePostLikeTask().execute(post);

                }
            }
        });

    }

    public class HotVoiceViewHolderItem{

        public LinkTextView postMessageView;
        public TextView numberCommentsView;
        public TextView companyNameView;
        public TextView numberLikesView;
        public Button likeButtonView;
        public ImageView likeButtonImageView;
        public Button optionButtonView;
        public ImageView optionImageView;
        public TextView relativeTimeView;


    }

}


