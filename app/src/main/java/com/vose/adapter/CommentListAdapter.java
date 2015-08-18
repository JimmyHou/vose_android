package com.vose.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.voice.app.R;
import com.parse.ParseUser;
import com.vose.AsyncTask.GetMostRecentlyLikedCommentIdsTask;
import com.vose.AsyncTask.UpdateCommentAndCommentLikeTask;
import com.vose.data.model.post.Comment;
import com.vose.data.model.post.Post;
import com.vose.helper.PrettyTimeHelper;
import com.vose.util.LinkTextView;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 2014/9/22.
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {
    private Context context;
    private List<Comment> comments;
    private List<String> alreadyLikedCommentIds;
    private Typeface typeface;

    public CommentListAdapter(Context context, int layoutResourceId, Post parentPost, List<Comment> comments) {
        super(context, layoutResourceId, comments);
        this.context = context;
        this.comments = comments;
        try {
            alreadyLikedCommentIds = new GetMostRecentlyLikedCommentIdsTask().execute(ParseUser.getCurrentUser()).get();

        } catch (Exception e) {
            Log.e("Failed to query most recently liked comment by userId " + ParseUser.getCurrentUser().getObjectId(), e.toString());
        }

        typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/copyfonts.com_gotham-light.ttf");
    }


    //need to refactor by holder item
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        com.vose.util.LinkTextView messageView = null;
        TextView relativeTimeViw = null;
        TextView commentNumberLikesView = null;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView  = inflater.inflate(R.layout.comment_item, parent,false);

            if(convertView!=null){

                CommentViewHolderItem holderItem = new CommentViewHolderItem();
                holderItem.messageView = messageView = (com.vose.util.LinkTextView)convertView.findViewById(R.id.comment_message);
                holderItem.relativeTimeViw = relativeTimeViw = (TextView)convertView.findViewById(R.id.comment_relative_time);
                holderItem.commentNumberLikesView = commentNumberLikesView = (TextView)convertView.findViewById(R.id.comment_number_likes);
                   // Store the holder with the view.
                convertView.setTag(holderItem);
            }
        }else {
            CommentViewHolderItem holderItem = (CommentViewHolderItem) convertView.getTag();
            messageView = holderItem.messageView;
            relativeTimeViw = holderItem.relativeTimeViw;
            commentNumberLikesView =holderItem.commentNumberLikesView;
        }

        if(convertView!=null){

            final Comment comment = comments.get(position);

            setViewsForItem(comment, messageView,relativeTimeViw,  commentNumberLikesView);

        }

         return convertView;
    }


    private void setViewsForItem(final Comment comment, com.vose.util.LinkTextView messageView, TextView relativeTimeViw,  final TextView commentNumberLikesView){

        messageView.setText(comment.getMessage());
        messageView.setMovementMethod(LinkTextView.LinkTextViewMovementMethod.getInstance());
        //messageView.setTypeface(typeface);

        relativeTimeViw.setText(PrettyTimeHelper.convertToSimplePrettyTime(comment.getCreatedAt()));

        commentNumberLikesView.setText(String.valueOf(comment.getNumberLikes()));

        if( alreadyLikedCommentIds.contains(comment.getObjectId())){
            commentNumberLikesView.setTag(Boolean.TRUE);
            commentNumberLikesView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_like_after, 0, 0, 0);
        }
        else
            //means not clicked yet
            commentNumberLikesView.setTag(Boolean.FALSE);

        //like on click listener
        commentNumberLikesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Boolean)commentNumberLikesView.getTag() == Boolean.FALSE){
                    commentNumberLikesView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_like_after, 0, 0, 0);

                    int currentNumberLikes = comment.getNumberLikes()+1;
                    comment.setNumberLikes(currentNumberLikes);
                    commentNumberLikesView.setText(String.valueOf(currentNumberLikes));

                    //already like it
                    commentNumberLikesView.setTag(Boolean.TRUE);
                    alreadyLikedCommentIds.add(comment.getObjectId());

                    new UpdateCommentAndCommentLikeTask().execute(comment);
                }else{

                    Utility.showShortToastMessage(context, context.getResources().getString(R.string.already_liked_comment));

                }
            }
        });

    }

    public class CommentViewHolderItem{

        public com.vose.util.LinkTextView messageView;
        public TextView relativeTimeViw;
        public TextView commentNumberLikesView;

    }
}
