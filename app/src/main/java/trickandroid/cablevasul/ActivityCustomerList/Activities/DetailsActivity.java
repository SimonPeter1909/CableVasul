package trickandroid.cablevasul.ActivityCustomerList.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import trickandroid.cablevasul.R;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";

    private TextView nameTV;
    private TextView monthlyAmountTV;
    private TextView connectionNumberTV;
    private TextView mobileNumberTV;
    private TextView aatharNumberTV;
    private TextView cafNumberTV;
    private TextView setUpBoxNumberTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setUpToolbar();
        initializeWidgets();
        setTexts();
    }

    private void setTexts(){
        nameTV.setText(getStringExtra("name"));
        connectionNumberTV.setText(getStringExtra("connectionNumber"));
        monthlyAmountTV.setText(getStringExtra("monthlyAmount"));
        mobileNumberTV.setText(getStringExtra("mobileNumber"));
        aatharNumberTV.setText(getStringExtra("aatharNumber"));
        cafNumberTV.setText(getStringExtra("cafNumber"));
        setUpBoxNumberTV.setText(getStringExtra("setUpBoxNumber"));
    }

    private String getStringExtra(String key){
        switch (key) {
            case "date":
                return getIntent().getStringExtra("date");
            case "areaName":
                return getIntent().getStringExtra("areaName");
            case "connectionNumber":
                return getIntent().getStringExtra("connectionNumber");
            case "monthlyAmount":
                return getIntent().getStringExtra("monthlyAmount");
            case "name":
                return getIntent().getStringExtra("name");
            case "mobileNumber":
                return getIntent().getStringExtra("mobileNumber");
            case "aatharNumber":
                return getIntent().getStringExtra("aatharNumber");
            case "cafNumber":
                return getIntent().getStringExtra("cafNumber");
            case "setUpBoxNumber":
                return getIntent().getStringExtra("setUpBoxNumber");
            case "paidDate":
                return getIntent().getStringExtra("paidDate");
        }
        return key;
    }

    private void initializeWidgets(){
        nameTV = findViewById(R.id.nameTV);
        monthlyAmountTV = findViewById(R.id.monthlyAmountTV);
        connectionNumberTV = findViewById(R.id.connectionNumberTV);
        mobileNumberTV = findViewById(R.id.mobileNumberTV);
        aatharNumberTV = findViewById(R.id.aadharNumberTV);
        cafNumberTV = findViewById(R.id.cafTV);
        setUpBoxNumberTV = findViewById(R.id.setUpSerialTV);
    }

    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbarDetailsActivity);
        toolbar.setTitle(getStringExtra("name"));
        toolbar.setSubtitle("Connection Number : " + getStringExtra("connectionNumber"));
        toolbar.setNavigationIcon(R.drawable.ic_person);
        TextView paidTV = findViewById(R.id.paidTV);
        TextView dateTV = findViewById(R.id.newConnectionDateTV);
        if (getStringExtra("paidDate").equals("Unpaid")){
            toolbar.setBackgroundColor(getResources().getColor(R.color.Red));
            paidTV.setText("Connection Date");
            dateTV.setText(getStringExtra("date"));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Green));
            paidTV.setText("Paid On");
            dateTV.setText(getStringExtra("paidDate"));
        }
        setSupportActionBar(toolbar);
    }

}
