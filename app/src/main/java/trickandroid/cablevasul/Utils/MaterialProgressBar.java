package trickandroid.cablevasul.Utils;

import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import trickandroid.cablevasul.ActivityCustomerList.Activities.EditConnectionActivity;

/**
 * Created by Simon on 3/22/2018.
 */

public class MaterialProgressBar {

    private static final String TAG = "MaterialProgressBar";

    public MaterialDialog progressBar;

    /**
     * displays progressDialog till the Firebase values are Loaded
     */
    public void startProgressBar(Context context){
        MaterialDialog.Builder progressBuilder = new MaterialDialog.Builder(context)
                .title("Loading")
                .content("Please Wait...")
                .progress(true,0)
                .progressIndeterminateStyle(true);

        progressBar = progressBuilder.build();
        progressBar.setCancelable(false);
        progressBar.show();
        Log.d(TAG, "startProgressBar: progress bar Started");
    }

    public void stopProgressBar(){
        progressBar.dismiss();
        Log.d(TAG, "stopProgressBar: progress bar dismissed");
    }
}
