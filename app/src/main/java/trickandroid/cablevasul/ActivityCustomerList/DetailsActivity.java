package trickandroid.cablevasul.ActivityCustomerList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import trickandroid.cablevasul.R;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";

    //widgets


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setUpToolbar();

    }

    private String getStringExtra(String key){
        switch (key) {
            case "connectionNumber":
                return getIntent().getExtras().getString("connectionNumber");
            case "monthlyAmount":
                return getIntent().getExtras().getString("monthlyAmount");
            case "name":
                return getIntent().getExtras().getString("name");
            case "mobileNumber":
                return getIntent().getExtras().getString("mobileNumber");
            case "aatharNumber":
                return getIntent().getExtras().getString("aatharNumber");
            case "cafNumber":
                return getIntent().getExtras().getString("cafNumber");
            case "setUpBoxNumber":
                return getIntent().getExtras().getString("setUpBoxNumber");
            case "paidDate":
                return getIntent().getExtras().getString("paidDate");
        }
        return key;
    }


    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getStringExtra("name"));
        toolbar.setSubtitle(getStringExtra("connectionNumber"));
        if (getStringExtra("paidDate").equals("Unpaid")){
            toolbar.setBackgroundColor(getResources().getColor(R.color.Red));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Green));
        }
        setSupportActionBar(toolbar);
    }

}
