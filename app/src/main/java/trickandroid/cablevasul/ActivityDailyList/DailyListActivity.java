package trickandroid.cablevasul.ActivityDailyList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import trickandroid.cablevasul.ActivityArea.AreaFragments.FragmentAreaList;
import trickandroid.cablevasul.ActivityArea.AreaFragments.FragmentDailyList;
import trickandroid.cablevasul.ActivityArea.AreaFragments.FragmentMonthList;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentConnectionList;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentPaidList;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentPendingList;
import trickandroid.cablevasul.ActivityDailyList.Fragments.FragmentDailyPaidList;
import trickandroid.cablevasul.ActivityDailyList.Fragments.FragmentDailyPendingList;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.FirebasePackage.InitializeFirebaseAuth;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.SectionPagerAdapter;

public class DailyListActivity extends AppCompatActivity {
    private static final String TAG = "DailyListActivity";

    //widgets
    private TextView totalConnectionsTV, pendingConnectionsTV;

    //progressBar
    private MaterialDialog progressBar;

    //firebase
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private InitializeFirebaseAuth auth = new InitializeFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_list);
        auth.initializeFBAuth();
        setProgressBar();
        setUpToolbar();
        initializeWidgets();
        setupViewPager();
        setTextViewValues();
    }

    /**
     * loads value from database and sets in TextView
     */
    private void setTextViewValues(){
        nodes.getNodeDailyList().child(getDate().replace("/",",")).child(getAreaName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    int totalConnections = dataSnapshot.child("totalConnections").getValue(Integer.class);
                    totalConnectionsTV.setText(String.valueOf(totalConnections));

                    int pendingConnections = dataSnapshot.child("pendingConnections").getValue(Integer.class);
                    pendingConnectionsTV.setText(String.valueOf(pendingConnections));
                progressBar.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * displays progressDialog till the Firebase values are Loaded
     */
    public void setProgressBar(){
        MaterialDialog.Builder progressBuilder = new MaterialDialog.Builder(this)
                .title("Loading")
                .content("Please Wait...")
                .progress(true,0)
                .progressIndeterminateStyle(true);

        progressBar = progressBuilder.build();
        progressBar.setCancelable(false);
        progressBar.show();
    }

    /**
     * initialize Widgets
     */
    private void initializeWidgets(){
        totalConnectionsTV = findViewById(R.id.totalConnectionTV);
        pendingConnectionsTV = findViewById(R.id.pendingTV);
    }

    /**
     * sets up view pager
     */
    private void setupViewPager() {
        Bundle bundle = new Bundle();
        bundle.putString("areaName", getAreaName());
        bundle.putString("date",getDate());

        android.support.v4.app.Fragment pendingList = FragmentDailyPendingList.newInstance();
        pendingList.setArguments(bundle);

        android.support.v4.app.Fragment paidList = FragmentDailyPaidList.newInstance();
        paidList.setArguments(bundle);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.addFragments(pendingList, getAreaName());
        sectionPagerAdapter.addFragments(paidList, getAreaName());

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.toolbarTabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Pending List");
        tabLayout.getTabAt(1).setText("Paid List");
    }

    /**
     * get String value of date from Intent
     * @return
     */
    private String getDate(){
        return getIntent().getStringExtra("date");
    }

    /**
     * get Sting value of areaName form Intent
     * @return
     */
    private String getAreaName(){
        return getIntent().getStringExtra("areaName");
    }


    /**
     * setsUp toolbar
     */
    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbarDailyList);
        toolbar.setTitle(getDate());
        toolbar.setSubtitle(getAreaName());
        toolbar.setNavigationIcon(R.drawable.ic_calendar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthListner();
    }
}
