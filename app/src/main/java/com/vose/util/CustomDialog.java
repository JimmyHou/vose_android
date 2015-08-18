package com.vose.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.voice.app.R;

/**
 * Created by jimmyhou on 1/12/15.
 */
public class CustomDialog {

    public interface CustomDialogDelegate{

        public void onComplete(int buttonIndex);
    }

    public static synchronized Dialog getLoadingProgressDialog(Activity activity, String message){

        Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	// do this before adding content else exception

        // set the custom content
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);

        TextView statusText = (TextView) dialog.findViewById(R.id.message);
        statusText.setText(message);

        dialog.show();

        return dialog;
    }


    public static void showOKReminderDialog(Activity activity, String message){

        final Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	// do this before adding content else exception

        // set the custom content
        dialog.setContentView(R.layout.dialog_reminder);
        dialog.setCancelable(false);

        TextView statusText = (TextView) dialog.findViewById(R.id.message);
        statusText.setText(message);

        Button okButton = (Button) dialog.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showOKReminderDialogWithDelegate(Activity activity, String message, final CustomDialogDelegate callback){

        final Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	// do this before adding content else exception

        // set the custom content
        dialog.setContentView(R.layout.dialog_reminder);
        dialog.setCancelable(false);

        TextView statusText = (TextView) dialog.findViewById(R.id.message);
        statusText.setText(message);

        Button okButton = (Button) dialog.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null){

                    callback.onComplete(0);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showTwoButtonsDialog(Context context, String titleString, String message, String rightButtonString, String leftButtonString, final CustomDialogDelegate callback
    ){

        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	// do this before adding content else exception

        // set the custom content
        dialog.setContentView(R.layout.dialog_twobuttons_edittext);
        dialog.setCancelable(false);

        if(!Utility.isEmptyString(titleString)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            titleView.setText(titleString);
            titleView.setVisibility(View.VISIBLE);
        }

        if(!Utility.isEmptyString(message)){
            TextView contents = (TextView) dialog.findViewById(R.id.message);
            contents.setVisibility(View.VISIBLE);
            contents.setText(message);
        }


        Button rightButton = (Button) dialog.findViewById(R.id.right_button);

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null){

                    callback.onComplete(0);
                }

                dialog.dismiss();
            }
        });

        rightButton.setText(rightButtonString);

        Button leftButton = (Button) dialog.findViewById(R.id.left_button);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(callback!=null){

                    callback.onComplete(1);
                }

                dialog.dismiss();
            }
        });

        leftButton.setText(leftButtonString);

        dialog.show();

    }




    public static Dialog getTwoButtonsEditTextDialog(Activity activity, String title){

        final Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	// do this before adding content else exception

        // set the custom content
        dialog.setContentView(R.layout.dialog_twobuttons_edittext);
        dialog.setCancelable(false);

        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);

        EditText inputMessage = (EditText) dialog.findViewById(R.id.input_message);
        inputMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        Button cancelButton = (Button) dialog.findViewById(R.id.left_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }


    public static void hideDialog(Dialog dialog){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

}
