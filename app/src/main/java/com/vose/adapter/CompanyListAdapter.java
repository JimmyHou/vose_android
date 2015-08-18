package com.vose.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.voice.app.R;
import com.vose.AsyncTask.CreateUserFollowCompanyTask;
import com.vose.AsyncTask.UnfollowCompanyTask;
import com.vose.cache.CompanyCheckTimeMapCache;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.data.model.company.Company;
import com.vose.util.CustomDialog;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/8/23.
 */


public class CompanyListAdapter extends ArrayAdapter<Company> {

    private Context context;
    private Map<String, Date> companyCheckTimeMap;
    private List<Company> companies;
    private boolean showFollowIcon;
    private boolean showNewPostMark;
    private boolean showIndustryName;


    public CompanyListAdapter(Context context, int layoutResourceId, List<Company> companies, boolean showIndustryName, boolean showFollowIcon,boolean showNewPostMark) {
        super(context, layoutResourceId, companies);
        this.context = context;
        this.companies = companies;
        this.showFollowIcon = showFollowIcon;
        this.showIndustryName = showIndustryName;
        this.showNewPostMark = showNewPostMark;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

       ImageView hasNewPostMark = null;
       TextView companyNameView = null;
       TextView industryNameView = null;
       ImageView followCompanyView = null;
       companyCheckTimeMap = CompanyCheckTimeMapCache.getInstance(context).getCompanyCheckTimeMap();

        if(convertView == null){
           LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           convertView = inflater.inflate(R.layout.company_item, parent,false);

           if (convertView != null) {
             //set up ViewHolder
             ViewHolderItem holderItem = new ViewHolderItem ();
             holderItem.hasNewPostMark = hasNewPostMark = (ImageView) convertView.findViewById(R.id.has_new_post_mark);
             holderItem.companyNameView = companyNameView = (TextView) convertView.findViewById(R.id.company_name);
             holderItem.industryNameView = industryNameView = (TextView) convertView.findViewById(R.id.industry_name);
             holderItem.followCompanyView = followCompanyView = (ImageView)convertView.findViewById(R.id.follow_company);
             convertView.setTag(holderItem);
            }
       }else{
           //we just avoid calling findViewById() on resource every time, passing the same convertView to get this view holder everytime
           ViewHolderItem holderItem = (ViewHolderItem) convertView.getTag();
           hasNewPostMark = holderItem.hasNewPostMark;
           companyNameView = holderItem.companyNameView;
           industryNameView = holderItem.industryNameView;
           followCompanyView = holderItem.followCompanyView;
       }

       final Company company = companies.get(position);

       Date checkTime = companyCheckTimeMap.get(company.getObjectId());

       Date companyPostTime = company.getPostTime();
       if(companyPostTime == null){
           companyPostTime = new Date();
       }

       if(showNewPostMark) {
           if (company.getNumberPosts() == 0 || checkTime != null && company.getPostTime() != null & checkTime.after(companyPostTime)) {
               hasNewPostMark.setVisibility(View.GONE);
           } else{
               hasNewPostMark.setVisibility(View.VISIBLE);
               hasNewPostMark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_has_new_post_dot));
           }
       }


       companyNameView.setText(company.getName());

       if(showIndustryName) {
           industryNameView.setText(Utility.mapIndustryCodeToName(company.getIndustryCode()));
       }else{
           industryNameView.setVisibility(View.GONE);
       }


       if(showFollowIcon) {
           setFollowCompany(convertView, followCompanyView, company);
       }


       return convertView;
   }


    private void setFollowCompany(View rowView, final ImageView followCompanyView, final Company company){

        followCompanyView.setVisibility(View.VISIBLE);
        if(FollowingCompaniesCache.getInstance().alreadyFollowThisCompany(company)){
            followCompanyView.setTag(true);
            followCompanyView.setBackground(context.getResources().getDrawable(R.drawable.ic_follow_after));
        }
        else {
            //means not clicked yet
            followCompanyView.setTag(false);
            followCompanyView.setBackground(context.getResources().getDrawable(R.drawable.ic_follow_before));
        }

        //follow companies
        followCompanyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) followCompanyView.getTag() == false) {
                    followCompanyView.setBackground(context.getResources().getDrawable(R.drawable.ic_follow_after));
                    //already like it
                    followCompanyView.setTag(Boolean.TRUE);

                    StringBuilder followCompanyMessageBuilder = new StringBuilder(context.getResources().getString(R.string.follow_company));
                    followCompanyMessageBuilder.append(" ");
                    followCompanyMessageBuilder.append(company.getName());

                    final Toast toast = Toast.makeText(context, followCompanyMessageBuilder.toString(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                    new CreateUserFollowCompanyTask().execute(company);
                    //the latest followed company is at the top
                    FollowingCompaniesCache.getInstance().getFollowingCompanies().add(0, company);
                } else {


//
//                    AlertDialog.Builder builder;
//                    //don't use context of this adapter, it's already taken, should use context of this root view
//                    builder = new AlertDialog.Builder(followCompanyView.getRootView().getContext());
//
//                    StringBuilder unfollowMessageBuilder = new StringBuilder(context.getResources().getString(R.string.unfollow_company));
//                    unfollowMessageBuilder.append(" ");
//                    unfollowMessageBuilder.append(company.getName());
//                    unfollowMessageBuilder.append("?");
//
//                    builder.setMessage(unfollowMessageBuilder.toString());
//                    builder.setCancelable(true);
//                    //unfollow company
//                    builder.setPositiveButton("Yes", new YesOnClickListener(company, followCompanyView));
//                    builder.setNegativeButton("No", new NoOnClickListener());
//                    AlertDialog dialog = builder.create();
//                    dialog.show();



                    CustomDialog.showTwoButtonsDialog(followCompanyView.getRootView().getContext(), context.getResources().getString(R.string.unfollow_company), null,

                            getContext().getString(R.string.Yes), getContext().getString(R.string.No), new CustomDialog.CustomDialogDelegate() {
                                @Override
                                public void onComplete(int buttonIndex) {
                                    //right button or positive button
                                    if (buttonIndex == 0) {

                                        followCompanyView.setTag(false);
                                        followCompanyView.setBackground(context.getResources().getDrawable(R.drawable.ic_follow_before));
                                        //need to remove by objectId, since some companies are created withonly name and id
                                        removeAnCompanyFromFollowingCompanyList(FollowingCompaniesCache.getInstance().getFollowingCompanies(), company);
                                        try {
                                            new UnfollowCompanyTask().execute(company);
                                        } catch (Exception e) {
                                            Log.e("failed to unfollow company: "+ company.getObjectId(), e.toString());
                                        }
                                    }


                                }
                            });

                }
            }
        });
    }


    private final class YesOnClickListener implements
            DialogInterface.OnClickListener {
        private  Company company;
        private  ImageView followCompanyView;

        YesOnClickListener(Company company, ImageView followCompanyView){
            super();
            this.company = company;
            this.followCompanyView = followCompanyView;
        }

        public void onClick(DialogInterface dialog, int which) {

            followCompanyView.setTag(false);
            followCompanyView.setBackground(context.getResources().getDrawable(R.drawable.ic_follow_before));
            //need to remove by objectId, since some companies are created withonly name and id
            removeAnCompanyFromFollowingCompanyList(FollowingCompaniesCache.getInstance().getFollowingCompanies(), company);
            try {
                new UnfollowCompanyTask().execute(company);
            } catch (Exception e) {
                  Log.e("failed to unfollow company: "+ company.getObjectId(), e.toString());
              }

        }
    }


    private void removeAnCompanyFromFollowingCompanyList(List<Company> followingCompanies, Company company){

        for(Company companyInList: followingCompanies){
            if(companyInList.getObjectId().equals(company.getObjectId())) {
                followingCompanies.remove(companyInList);
                //stop looping, otherwise ConcurrentModificationError
                break;
            }
        }
    }

    private final class NoOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {

        }
    }

    private class ViewHolderItem{

        ImageView hasNewPostMark;
        TextView companyNameView;
        TextView industryNameView;
        ImageView followCompanyView;

    }

}
