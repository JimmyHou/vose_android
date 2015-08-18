package com.vose.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.voice.app.R;
import com.vose.AsyncTask.UpdatePostAndAddPostLikeTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.cache.AlreadyLikedPostIdsCache;
import com.vose.data.model.post.Post;
import com.vose.helper.PrettyTimeHelper;
import com.vose.util.LinkTextView;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/21.
 */

// out of use now, change HotVoicesAdapter to PostListAdapter to replace CompanyPostListAdapter
public class CompanyPostListAdapter  extends ArrayAdapter<Post> {

    private MainTabHostActivity mainActivity;

    public CompanyPostListAdapter(MainTabHostActivity mainActivity, int layoutResourceId, List<Post> companyPosts) {

        super(mainActivity.getBaseContext(), layoutResourceId, companyPosts);

        this.mainActivity = mainActivity;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LinkTextView postMessageView = null;
        TextView numberCommentsView = null;
        TextView relativeTimeView = null;
        TextView numberLikesView = null;
        TextView optionView = null;
        TextView companyNameView = null;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mainActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hot_voice_item, parent,false);


            if(convertView!=null){
                CompanyPostListViewHolderItem holderItem = new CompanyPostListViewHolderItem();

                holderItem.postMessageView = postMessageView = (LinkTextView)convertView.findViewById(R.id.post_message);


                holderItem.numberCommentsView = numberCommentsView = (TextView) convertView.findViewById(R.id.number_comments);
                holderItem.relativeTimeView = relativeTimeView= (TextView)convertView.findViewById(R.id.post_relative_time);
                holderItem.numberLikesView = numberLikesView = (TextView)convertView.findViewById(R.id.number_likes);
                //holderItem.optionView = optionView = (TextView)convertView.findViewById(R.id.post_option_image);
                holderItem.companyNameView = companyNameView = (TextView) convertView.findViewById(R.id.company_name);

                convertView.setTag(holderItem);
            }

        }else{
            CompanyPostListViewHolderItem holderItem = (CompanyPostListViewHolderItem) convertView.getTag();

            postMessageView = holderItem.postMessageView;
            numberCommentsView = holderItem.numberCommentsView;
            relativeTimeView = holderItem.relativeTimeView;
            numberLikesView = holderItem.numberLikesView;
            optionView = holderItem.optionView;
            companyNameView = holderItem.companyNameView;
        }


        if(convertView!=null){

            companyNameView.setVisibility(View.GONE);

            final Post post = this.getItem(position);
            setViewsForItem(post, postMessageView, optionView,numberCommentsView, relativeTimeView, numberLikesView);
         }

        return convertView;
    }


    private void setViewsForItem(final Post post,  TextView postMessageView,  TextView optionView, TextView numberCommentsView, TextView relativeTimeView, final TextView numberLikesView){


        postMessageView.setText(post.getMessage());

        numberCommentsView.setText(String.valueOf(post.getNumberComments()));

        relativeTimeView.setText(PrettyTimeHelper.convertToSimplePrettyTime(post.getCreatedAt()));


        optionView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mainActivity.optionDialog(post);
            }
        });


        //set up number likes view and its listener
        numberLikesView.setText(String.valueOf(post.getNumberLikes()));

        //reset number likes view everytime unliked since it's from viewHolder!
        numberLikesView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_before, 0, 0, 0);
        //means not clicked yet
        numberLikesView.setTag(Boolean.FALSE);
        if(AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().contains(post.getObjectId())){
            numberLikesView.setTag(Boolean.TRUE);
            numberLikesView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_after, 0, 0, 0);
        }

        //like on click listener
        numberLikesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) numberLikesView.getTag() == Boolean.FALSE) {
                    numberLikesView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_after, 0, 0, 0);

                    int currentNumberLikes = post.getNumberLikes() + 1;
                    post.setNumberLikes(currentNumberLikes);
                    numberLikesView.setText(String.valueOf(currentNumberLikes));

                    //already like it
                    numberLikesView.setTag(Boolean.TRUE);

                    new UpdatePostAndAddPostLikeTask().execute(post);
                    AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds().add(post.getObjectId());
                    AlreadyLikedPostIdsCache.getInstance().setAlreadyLikedPostIds(AlreadyLikedPostIdsCache.getInstance().getAlreadyLikedPostIds());
                }else{
                    Utility.showShortToastMessage(mainActivity, mainActivity.getResources().getString(R.string.already_liked_post));

                }
            }
        });

    }

    //view holder used in getView()
    public class CompanyPostListViewHolderItem{

        public LinkTextView postMessageView ;

        public TextView numberCommentsView;

        public TextView relativeTimeView;

        public TextView numberLikesView;

        public TextView optionView;

        public TextView companyNameView;

    }
}
