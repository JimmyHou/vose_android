package com.vose.fragment.launch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.voice.app.R;
import com.vose.AsyncTask.CreateUserFollowCompaniesTask;
import com.vose.AsyncTask.GetHotCompaniesByIndustryCodeTask;
import com.vose.activity.MainTabHostActivity;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.data.model.company.Company;
import com.vose.util.CustomDialog;
import com.vose.util.Utility;

import java.util.List;

/**
 * Created by jimmyhou on 4/7/15.
 */
public class OnboardingFollowCompaniesFragment  extends ListFragment {



    // Enum and constants
    //-----------
    public static final String LOG_TAG = "OnboardingCompanies";
    public static final String FRAGMENT_TAG = "FRAGMENT_ONBOARDING_FOLLOW_COMPANIES_FRAGMENT";

    //-----------
    // Member vars
    //-------------

    private OnboardingFollowCompanyAdapter mAdapter = null;

    private List<Company> hotCompanies;
    private GetHotCompaniesByIndustryCodeTask getHotCompaniesByIndustryCodeTask;
    private Handler handler;

    private View rootView;
    private ListView fCompanyListView;
    private View fProgressSection;
    private View fFollowCompanySection;
    private Button fStartButton;


    public static OnboardingFollowCompaniesFragment newInstance(GetHotCompaniesByIndustryCodeTask getHotCompaniesByIndustryCodeTask){

        OnboardingFollowCompaniesFragment fragment = new OnboardingFollowCompaniesFragment();
        fragment.getHotCompaniesByIndustryCodeTask = getHotCompaniesByIndustryCodeTask;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        handler = new Handler();

        rootView = inflater.inflate(R.layout.fragment_onboarding_follow_companies, container, false);
        fProgressSection = rootView.findViewById(R.id.progressSection);
        fFollowCompanySection = rootView.findViewById(R.id.follow_company_section);
        fCompanyListView = (ListView) rootView.findViewById(android.R.id.list);
        fStartButton = (Button) rootView.findViewById(R.id.button_start_vose);

        setHasOptionsMenu(true);

        setStartButton();

        return rootView;
    }


    @Override
    public void onStart(){

        super.onStart();
        runGetHotCompaniesTask();

    }


    private void updateListView(List<Company> companies){

        mAdapter =  new OnboardingFollowCompanyAdapter(getActivity().getBaseContext(), android.R.layout.simple_list_item_multiple_choice, companies, false, true);

        setListAdapter(mAdapter);

    }

    private void setStartButton(){

        fStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Company> followingCompanies = FollowingCompaniesCache.getInstance().getFollowingCompanies();

                if(followingCompanies.size() <3){

                    CustomDialog.showOKReminderDialog(getActivity(), getActivity().getResources().getString(R.string.onboarding_follow_companies));

                    return;
                }

                CustomDialog.getLoadingProgressDialog(getActivity(), getResources().getString(R.string.loading_message)).show();


                new CreateUserFollowCompaniesTask().execute(followingCompanies);

                startMainActivity();
            }
        });
    }


    private void startMainActivity(){

        Intent companyTabHostActivity = new Intent(getActivity(), MainTabHostActivity.class);
        startActivity(companyTabHostActivity);

        getActivity().finish();
    }


    public void runGetHotCompaniesTask(){

        // use a call back finish onCreateView method first and callback later on
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        try {

                            hotCompanies = getHotCompaniesByIndustryCodeTask.get();
                            updateListView(hotCompanies);

                        } catch (Exception e) {
                            Log.e(LOG_TAG, Utility.getExceptionStackTrace(e));
                        }

                        fProgressSection.setVisibility(View.GONE);
                        fFollowCompanySection.setVisibility(View.VISIBLE);

                    }
                });
            }
        };
        new Thread(runnable).start();
    }


    private class OnboardingFollowCompanyAdapter extends ArrayAdapter<Company>{

        private Context context;
        private List<Company> companies;
        private boolean showFollowIcon;
        private boolean showIndustryName;


        public OnboardingFollowCompanyAdapter(Context context, int layoutResourceId, List<Company> companies, boolean showIndustryName, boolean showFollowIcon) {
            super(context, layoutResourceId, companies);
            this.context = context;
            this.companies = companies;
            this.showFollowIcon = showFollowIcon;
            this.showIndustryName = showIndustryName;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            View companyFrameView = null;
            TextView companyNameView = null;
            TextView industryNameView = null;
            ImageView followCompanyView = null;

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.company_item, parent,false);

                if (convertView != null) {
                    //set up ViewHolder
                    ViewHolderItem holderItem = new ViewHolderItem ();
                    holderItem.companyFrameView = companyFrameView = convertView.findViewById(R.id.company_frame);
                    holderItem.companyNameView = companyNameView = (TextView) convertView.findViewById(R.id.company_name);
                    holderItem.industryNameView = industryNameView = (TextView) convertView.findViewById(R.id.industry_name);
                    holderItem.followCompanyView = followCompanyView = (ImageView)convertView.findViewById(R.id.follow_company);
                    convertView.setTag(holderItem);
                }
            }else{
                //we just avoid calling findViewById() on resource every time, passing the same convertView to get this view holder everytime
                ViewHolderItem holderItem = (ViewHolderItem) convertView.getTag();
                companyFrameView = holderItem.companyFrameView;
                companyNameView = holderItem.companyNameView;
                industryNameView = holderItem.industryNameView;
                followCompanyView = holderItem.followCompanyView;
            }

            final Company company = companies.get(position);
            companyNameView.setText(company.getName());

            if(showIndustryName) {
                industryNameView.setText(Utility.mapIndustryCodeToName(company.getIndustryCode()));
            }else{
                industryNameView.setVisibility(View.GONE);
            }


            if(showFollowIcon) {
                setFollowCompany(convertView, companyFrameView, followCompanyView, company);
            }


            return convertView;
        }


        private void setFollowCompany(View rowView, final View companyFrameView, final ImageView followCompanyView, final Company company){

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
            companyFrameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((Boolean) followCompanyView.getTag() == false) {
                        followCompanyView.setBackground(context.getResources().getDrawable(R.drawable.ic_follow_after));
                        //already like it
                        followCompanyView.setTag(Boolean.TRUE);

                        FollowingCompaniesCache.getInstance().getFollowingCompanies().add(0, company);
                    } else {


                        followCompanyView.setTag(false);
                        followCompanyView.setBackground(context.getResources().getDrawable(R.drawable.ic_follow_before));
                        //need to remove by objectId, since some companies are created withonly name and id
                        removeAnCompanyFromFollowingCompanyList(FollowingCompaniesCache.getInstance().getFollowingCompanies(), company);


                    }
                }
            });
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

        private class ViewHolderItem{

            View companyFrameView;
            TextView companyNameView;
            TextView industryNameView;
            ImageView followCompanyView;

        }


    }

}
