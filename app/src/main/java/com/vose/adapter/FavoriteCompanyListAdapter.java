package com.vose.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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
import com.vose.data.model.company.Company;
import com.vose.util.CustomDialog;
import com.vose.util.Utility;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/9/10. It's out of use now, already replaced by companyListAdaptor
 */
public class FavoriteCompanyListAdapter extends ArrayAdapter<Company> {


    private Activity activity;
    private List<Company> followingCompanies;
    private View fNoFollowingCompanyMessage;
    private Map<String, Date> companyCheckTimeMap;


    private FavoriteCompanyListAdapter thisAdapter;

    public FavoriteCompanyListAdapter(Activity context, int layoutResourceId, List<Company> followingCompanies, View fNoFollowingCompanyMessage) {
        super(context, layoutResourceId, followingCompanies);
        this.activity = context;
        this.followingCompanies = followingCompanies;
        this.thisAdapter = this;
        this.fNoFollowingCompanyMessage = fNoFollowingCompanyMessage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView hasNewPostMark = null;
        TextView companyNameView = null;
        TextView industryNameView = null;
        companyCheckTimeMap = CompanyCheckTimeMapCache.getInstance(getContext()).getCompanyCheckTimeMap();


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.company_item, parent, false);


            if (convertView != null) {
                //set up ViewHolder
                ViewHolderItem holderItem = new ViewHolderItem ();
                holderItem.hasNewPostMark = hasNewPostMark = (ImageView) convertView.findViewById(R.id.has_new_post_mark);
                holderItem.companyNameView = companyNameView = (TextView) convertView.findViewById(R.id.company_name);
                holderItem.industryNameView = industryNameView = (TextView) convertView.findViewById(R.id.industry_name);
                // Store the holder with the view.
                convertView.setTag(holderItem);

            }
        }else{
            //we just avoid calling findViewById() on resource everytime, passing the same convertView to get this view holder everytime
            ViewHolderItem holderItem = (ViewHolderItem) convertView.getTag();
            hasNewPostMark = holderItem.hasNewPostMark;
            companyNameView = holderItem.companyNameView;
            industryNameView = holderItem.industryNameView;
        }


        final Company company = followingCompanies.get(position);

        Date checkTime = companyCheckTimeMap.get(company.getObjectId());

        if(checkTime!=null&&company.getPostTime()!=null&checkTime.after(company.getPostTime())){
            hasNewPostMark.setVisibility(View.GONE);
        }else if(company.getNumberPosts()!=0){
            hasNewPostMark.setVisibility(View.VISIBLE);
        }

        companyNameView.setText(company.getName());
        industryNameView.setText(Utility.mapIndustryCodeToName(company.getIndustryCode()));



        final ImageView followCompanyView = (ImageView) convertView.findViewById(R.id.follow_company);

        followCompanyView.setVisibility(View.VISIBLE);
        followCompanyView.setTag(Boolean.TRUE);
        followCompanyView.setBackground(activity.getResources().getDrawable(R.drawable.ic_follow_after));

        //means not clicked yet
        if(isInFollowingCompanies(followingCompanies, company)){
            followCompanyView.setTag(true);
            followCompanyView.setBackground(activity.getResources().getDrawable(R.drawable.ic_follow_after));
        }
        else
            //means not clicked yet
            followCompanyView.setTag(false);



        followCompanyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Boolean)followCompanyView.getTag() == false){
                    followCompanyView.setBackground(activity.getResources().getDrawable(R.drawable.ic_follow_after));
                    //already like it
                    followCompanyView.setTag(Boolean.TRUE);

                    String followCompanyMessage = activity.getResources().getString(R.string.follow_company);


                    final Toast toast = Toast.makeText(activity, followCompanyMessage, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                    new CreateUserFollowCompanyTask().execute(company);
                    followingCompanies.add(company);

                }
                else{


                    CustomDialog.showTwoButtonsDialog(activity, activity.getResources().getString(R.string.unfollow_company), null,

                            getContext().getString(R.string.Yes), getContext().getString(R.string.No), new CustomDialog.CustomDialogDelegate() {
                                @Override
                                public void onComplete(int buttonIndex) {
                                    //right button or positive button
                                    if (buttonIndex == 0) {

                                        followCompanyView.setTag(false);
                                        new UnfollowCompanyTask().execute(company);
                                        //remove from the list and refresh the adapter by notifyDataSetChanged
                                        followingCompanies.remove(company);

                                        //if the last following company is removed
                                        if(followingCompanies.size() == 0){
                                            if(followingCompanies ==null || followingCompanies.size()==0) {

                                                fNoFollowingCompanyMessage.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        thisAdapter.notifyDataSetChanged();
                                    }


                                }
                            });

                }
            }
        });


        return convertView;
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
            new UnfollowCompanyTask().execute(company);
            //remove from the list and refresh the adapter by notifyDataSetChanged
            followingCompanies.remove(company);

            //if the last following company is removed
            if(followingCompanies.size() == 0){
                if(followingCompanies ==null || followingCompanies.size()==0) {

                    fNoFollowingCompanyMessage.setVisibility(View.VISIBLE);
                }
            }

            thisAdapter.notifyDataSetChanged();

        }
    }

    private boolean isInFollowingCompanies(List<Company> followingCompanies, Company company){
        for(Company followingCompany: followingCompanies){
            if(followingCompany.getObjectId().equals(company.getObjectId()))
                return  true;
        }
        return false;
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

    }

}
