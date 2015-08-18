package com.vose.customizedUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.vose.util.Constants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jimmyhou on 2014/11/30.
 *
 * copied from: http://v4all123.blogspot.in/2013/09/spinner-with-multiple-selection-in.html
 *
 */
public class MultiSelectionSpinner extends Spinner implements DialogInterface.OnMultiChoiceClickListener {

    private final String LOG_TAG = "MultiSelectionSpinner";

    String[] _items = null;
    boolean[] mSelection = null;

    ArrayAdapter<String> adapter;


    public MultiSelectionSpinner(Context context){
        super(context);

        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs){
        super(context,attrs);

        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);

    }


    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if(mSelection!= null && which < mSelection.length){
            mSelection[which] = isChecked;

            adapter.clear();
            adapter.add(buildSelectedItemString());

        }else{
            throw  new IllegalArgumentException(LOG_TAG + ":Argument 'which' is out of bounds.");
        }

    }

    @Override
    public boolean performClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items,mSelection,this).show();
        return  true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                LOG_TAG + ":setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items){
        _items = items;
        mSelection = new boolean[_items.length];
        adapter.clear();
        adapter.add(_items[0]);
        Arrays.fill(mSelection,false);
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                }
            }
        }
    }

    public void setSelection(List<String> selection) {
        adapter.clear();

        if(selection == null){
            return;
        }

        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }

        adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        adapter.clear();
        adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        adapter.clear();
        adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }


    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(Constants.LOCATION_NAME_COMMA);
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(Constants.LOCATION_NAME_COMMA);
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        onDetachedFromWindow();
    }

}

