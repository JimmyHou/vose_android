package com.vose.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.voice.app.R;

import java.util.List;

/**
 * Created by jimmyhou on 12/20/14.
 */

public class IndustrySpinnerAdapter extends ArrayAdapter<String>
{

    //check out app theme to get the design??

    private List<String> mAllIndustryNames;

    public IndustrySpinnerAdapter(Context invokingActivityContext, List<String> mAllIndustryNames)
    {
        super(invokingActivityContext, R.layout.action_bar_industry_spinner_item, mAllIndustryNames);

        this.mAllIndustryNames = mAllIndustryNames;
    }


    //set selected view on action bar!
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.action_bar_industry_spinner_item, parent, false);
        }

        if (null != convertView)
        {
            // Get the selected position.
            int selectedPosition = ((AdapterView) parent).getSelectedItemPosition();

            TextView industryNameView = (TextView) convertView.findViewById(R.id.industry_spinner_dropdown_name);

            String selectedIndustryName =  mAllIndustryNames .get(selectedPosition);

            if (null !=  selectedIndustryName)
            {
                //trim "All companies" & "Following companies" cases when it's selected
                // the head space is a hack to make the selectedIndustryName align with dropdown item vertically
                industryNameView.setText("   "+selectedIndustryName.split(" ")[0]);
            }
        }

        return convertView;
    }


    //set each single item view for drop down
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        String industryName = mAllIndustryNames.get(position);
        // Menu Item
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.action_bar_industry_spinner_dropdown, parent, false);

        TextView industryNameView = (TextView) convertView.findViewById(R.id.industry_name);
        industryNameView.setText(industryName);

        return convertView;
    }

}