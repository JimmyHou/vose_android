package com.vose.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.voice.app.R;
import com.vose.core.data.dao.company.CompanyNameObjectIdPair;

import java.util.List;

/**
 * Created by jimmyhou on 2014/12/17.
 */


 public  class SelectToPostCompanyListAdapter extends ArrayAdapter<CompanyNameObjectIdPair> {

        private Context context;
        private List<CompanyNameObjectIdPair> companyNameObjectIdPairs;



        public SelectToPostCompanyListAdapter(Context context, int layoutResourceId, List<CompanyNameObjectIdPair> companyNameObjectIdPairs) {
            super(context, layoutResourceId, companyNameObjectIdPairs);
            this.context = context;
            this.companyNameObjectIdPairs = companyNameObjectIdPairs;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            final CompanyNameObjectIdPair companyNameObjectIdPair = companyNameObjectIdPairs.get(position);
            TextView companyNameView = null;

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView= inflater.inflate(R.layout.select_company_item, parent,false);


                if(convertView!=null){
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.companyNameView = companyNameView = (TextView)convertView.findViewById(R.id.company_name);
                    convertView.setTag(viewHolder);
                }

            }else{
                ViewHolder viewHolder = ( ViewHolder) convertView.getTag();

                companyNameView = viewHolder.companyNameView;
            }

            companyNameView.setText(companyNameObjectIdPair.getCompanyName());

            return convertView;

        }



     private class ViewHolder{

         TextView companyNameView;

     }
}
