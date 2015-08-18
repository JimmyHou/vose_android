package com.vose.util;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.vose.AsyncTask.GetHotVoicesByCompaniesBeforeCreatedTimeTask;
import com.vose.AsyncTask.GetHotVoicesByIndustryBeforeCreatedTimeTask;
import com.vose.cache.AllCompaniesCache;
import com.vose.cache.FollowingCompaniesCache;
import com.vose.cache.IndustriesCache;
import com.vose.cache.NewCreatedPostsCache;
import com.vose.core.data.dao.company.CompanyBasedLocationDAOImpl;
import com.vose.core.data.dao.company.CompanyDAOImpl;
import com.vose.core.data.dao.company.CompanyNameObjectIdPair;
import com.vose.core.data.service.company.CompanyBasedLocationService;
import com.vose.core.data.service.company.CompanyBasedLocationServiceImpl;
import com.vose.core.data.service.company.CompanyService;
import com.vose.core.data.service.company.CompanyServiceImpl;
import com.vose.data.model.company.Company;
import com.vose.data.model.company.ParseIndustry;
import com.vose.data.model.post.Post;
import com.vose.data.model.util.ExtraIndustry;
import com.vose.data.model.util.UserStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jimmyhou on 2014/12/7.
 */
public class Utility {

    public static boolean stringIsEmpty(String str){
        if(str == null || str.trim().equals("")) {
            return true;
        }

        return  false;
    }

    public static <E> boolean listIsEmpty(List<E> list){
        if(list == null || list.isEmpty()) {
            return true;
        }

        return  false;
    }

    public static boolean isEmptyString(String str){

        if(str == null || str.trim().equals("")){
            return true;
        }
        else return false;
    }

    public static boolean isExtraIndustryCode(String industryCode){

        if(!industryCode.equals(ExtraIndustry.ALL.getCode()) &&!industryCode.equals(ExtraIndustry.FOLLOWING.getCode())){
            return false;
        }
        return true;
    }

    public static boolean isExtraIndustryName(String industryName){

        if(!industryName.equals(ExtraIndustry.ALL.getName()) &&!industryName.equals(ExtraIndustry.FOLLOWING.getName())){
            return false;
        }
        return true;
    }

    public static  String makeActionBarTitle(String title){

        StringBuilder stringBuilder = new StringBuilder(" ");
        stringBuilder.append(title);
        stringBuilder.append(" ");

        return stringBuilder.toString();
    }

    public static void showShortToastMessage(Context context, String message){
        Toast emptyCompanyNameToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        emptyCompanyNameToast.setGravity(Gravity.CENTER, 0, 0);
        emptyCompanyNameToast.show();
    }


    public static void resetParameters(){

        NewCreatedPostsCache.getInstance().clear();

    }

    public static void populateCompanyLocationTable(){
        //creating company location entity!

        //Done with Facebook, Google, MS, Yahoo, Oracle, Amazon,Baker & McKenzie, Holland & Knight
        String[] companyIdArray = {};
        List<String> companyIds = Arrays.asList(companyIdArray);

        String[] locationNameArray = {"San Francisco", "New York", "Miami", "Chicago", "Los Angels", "Seattle", "Boston", "Washington DC"};
        List<String> locationNames = Arrays.asList(locationNameArray);

        createCompanyBasedLocationToAllLocations(companyIds, locationNames);

    }

    public static void createCompanyBasedLocationToAllLocations(List<String> companyIds, List<String> locationNames){
        CompanyService companyService = new CompanyServiceImpl(new CompanyDAOImpl());
        CompanyBasedLocationService companyBasedLocationService = new CompanyBasedLocationServiceImpl(new CompanyBasedLocationDAOImpl(), new CompanyDAOImpl());

        List<Company> companies = null;

        try {
            companies=  companyService.getCompaniesByObjectIds(companyIds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(Company company : companies){
            for(String locationName: locationNames) {
                companyBasedLocationService.createCompanyBasedLocation(company, locationName);
            }
        }

    }

    public static List<CompanyNameObjectIdPair> convertCompaniesToCompanyNameObjectIdPairs(List<Company> companies){

        List<CompanyNameObjectIdPair> companyNameObjectIdPairs = new ArrayList<CompanyNameObjectIdPair>();

        for(Company company : companies){

            companyNameObjectIdPairs.add(new CompanyNameObjectIdPair(company));
        }


        return companyNameObjectIdPairs ;

    }


    public static int getIndustryItemPosition(String selectedIndustryCode, List<ParseIndustry> industries){

        for(int i = 0; i< industries.size();i++){

            if(industries.get(i).getIndustryCode().equals(selectedIndustryCode)){
                return i;
            }

        }

        return 0;
    }

    public static int getIndustryItemPositionByCodes(String selectedIndustryCode, List<String> codes){

        for(int i = 0; i< codes.size();i++){

            if(codes.get(i).equals(selectedIndustryCode)){
                return i;
            }

        }

        return 0;
    }



    public static List<String> populateIndustryNameWithoutExtra(){

        List<String> industryNameList = new ArrayList<String>();

        for(ParseIndustry industry: IndustriesCache.getInstance().getIndustries()){

            if(!Utility.isExtraIndustryCode(industry.getIndustryCode())){

                industryNameList.add(industry.getName());
            }
        }

        return industryNameList;
    }

    public  static String mapIndustryNameToCode(String selectedIndustryName){

        selectedIndustryName = selectedIndustryName.trim();

        for (ParseIndustry industry: IndustriesCache.getInstance().getIndustries()){
            if(industry.getName().equals(selectedIndustryName))
                return industry.getIndustryCode();
        }

        return  ExtraIndustry.OTHER.getCode();
    }

    public  static String mapIndustryCodeToName(String selectedIndustryCode){

        if(selectedIndustryCode == null){
            return null;
        }

        for (ParseIndustry industry: IndustriesCache.getInstance().getIndustries()){
            if(industry.getIndustryCode().equals(selectedIndustryCode))
                return industry.getName();
        }

        return selectedIndustryCode;
    }

    public  static  List<Company> SearchCompanyInCache(String companyName) {

        List<Company> matchedCompanies = new ArrayList<Company>();

        //could have a better way to search??
        for(Company company : AllCompaniesCache.getInstance().getAllCompanies()){
            if(company.getName().toLowerCase().contains(companyName.toLowerCase())){
                matchedCompanies.add(company);
            }
        }

        return matchedCompanies;
    }

    public static String getExceptionStackTrace(final Throwable throwable) {

        if(throwable == null){
            return "";
        }

        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();

    }

    public static List<Post> distinctJoinNewPosts(String currentIndustryCode, List<Post> oldPosts, List<Post> newPosts){

       if(listIsEmpty(oldPosts)){
           return newPosts;
       }

       if(listIsEmpty(newPosts)){
           return oldPosts;
       }
//
//       Set<Post> postSet = new LinkedHashSet<Post>(posts1);
//       postSet.addAll(posts2);
//
//       return  new ArrayList<Post>(postSet);

        List<Post> distinctiveNewPosts = new ArrayList<Post>();
        for(Post newPost : newPosts){

            boolean isDuplicate = false;

            for (Post oldPost : oldPosts){
                //duplicate
                if(oldPost.getObjectId().equals(newPost.getObjectId())){

                    isDuplicate = true;
                    break;
                }
            }

            if(!isDuplicate && (currentIndustryCode == null ||currentIndustryCode.equals(newPost.getIndustryCode()))){
                //add to the end since the latest posts are on the top
                distinctiveNewPosts.add(newPost);
            }
        }

        oldPosts.addAll(0,  distinctiveNewPosts);

        return  oldPosts;
    }

    public static List<Post> addNewPostToTop(List<Post> posts, Post addedPost){

        if(addedPost==null){
            return posts;
        }

        for(Post post: posts){

            if(post.getObjectId().equals(addedPost.getObjectId())){
                return posts;
            }
        }

        posts.add(0,addedPost);

        return posts;

    }

    public static boolean isUserBlocked() {

        String userStatus = ParseUser.getCurrentUser().getString(ParseDatabaseColumnNames.USER_STATUS);

        if (userStatus != null && userStatus.equals(UserStatus.BLOCKED.getCode())) {

            return true;
        }

        return false;

    }

    public static List<Post> loadHotVoices(String industryCode, Date createdTime , String logTag){

        List<Post> hotVoices = null;

        try {

            if(industryCode.equals(ExtraIndustry.FOLLOWING.getCode())){

                hotVoices  =  new GetHotVoicesByCompaniesBeforeCreatedTimeTask(createdTime).execute(FollowingCompaniesCache.getInstance().getFollowingCompanies()).get();

            }else{

                hotVoices  =  new GetHotVoicesByIndustryBeforeCreatedTimeTask(createdTime).execute(industryCode).get();

            }

        } catch (Exception e) {
            Log.e(logTag, Utility.getExceptionStackTrace(e));
        }

        return hotVoices;
    }


    public static void trackParseEvent(String eventName, String parameterName, String parameterValue, String viewSource){


        Map<String, String> dimensions = new HashMap<String, String>();

        if(!isEmptyString(parameterName) && !isEmptyString(parameterValue)) {
            dimensions.put(parameterName, parameterValue);
        }

        if(!isEmptyString(viewSource)){

            dimensions.put(AnalyticsConstants.VIEW_SOURCE, viewSource);
        }




        ParseAnalytics.trackEvent(eventName, dimensions);
    }

}
