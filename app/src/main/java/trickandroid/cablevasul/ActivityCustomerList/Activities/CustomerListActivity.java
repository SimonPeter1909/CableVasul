package trickandroid.cablevasul.ActivityCustomerList.Activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentConnectionList;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentPaidList;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentPendingList;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.FirebasePackage.InitializeFirebaseAuth;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;
import trickandroid.cablevasul.Utils.SectionPagerAdapter;
import trickandroid.cablevasul.Utils.ShowSnackBar;

public class CustomerListActivity extends AppCompatActivity {
    private static final String TAG = "CustomerListActivity";

    //firebase
    private InitializeFirebaseAuth auth = new InitializeFirebaseAuth();
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private FirebaseAnalytics mFirebaseAnalytics;

    //utils
    private ShowSnackBar snackBar = new ShowSnackBar();
    private DateSetter dateSetter = new DateSetter();

    //ProgressBar
    private MaterialDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        setToolbar();
        setProgressBar();
        setupBottomNavigationView();
        auth.initializeFBAuth();
        setupViewPager();
        cancelProgressBar();
    }

    /**
     * displays progressDialog till the Firebase values are Loaded
     */
    public void setProgressBar() {
        MaterialDialog.Builder progressBuilder = new MaterialDialog.Builder(this)
                .title("Loading")
                .content("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true);

        progressBar = progressBuilder.build();
        progressBar.setCancelable(false);
        progressBar.show();
    }

    /**
     * gets area name from AreaActivity and stores as String
     *
     * @return
     */
    public String getAreaName() {
        Intent intent = getIntent();
        return intent.getStringExtra("areaName");
    }

    /**
     * sets up toolbar
     */
    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getAreaName());
        toolbar.setSubtitle(dateSetter.ddmmyyyyday());
        setSupportActionBar(toolbar);
    }

    /**
     * Cancels progressBar after the firebase values are loaded
     */
    public void cancelProgressBar() {
        nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.dismiss();
                } else {
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * setting and customizing Bottom navigation View
     */
    public void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: starting");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigationBar);
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bottomNavigationViewEx.setIconTintList(0, getColorStateList(R.color.Green));
            bottomNavigationViewEx.setIconTintList(1, getColorStateList(R.color.Brown));
            bottomNavigationViewEx.setIconTintList(2, getColorStateList(R.color.Blue));
            bottomNavigationViewEx.setIconTintList(3, getColorStateList(R.color.Red));
            bottomNavigationViewEx.setIconTintList(4, getColorStateList(R.color.DarkOrange));
            bottomNavigationViewEx.setTextTintList(0, getColorStateList(R.color.Green));
            bottomNavigationViewEx.setTextTintList(1, getColorStateList(R.color.Brown));
            bottomNavigationViewEx.setTextTintList(2, getColorStateList(R.color.Blue));
            bottomNavigationViewEx.setTextTintList(3, getColorStateList(R.color.Red));
            bottomNavigationViewEx.setTextTintList(4, getColorStateList(R.color.DarkOrange));
        }
        onNavigationItemSelected(bottomNavigationViewEx);
    }

    /**
     * displays corresponding Dialog Box when the Item is clicked
     *
     * @param bottomNavigationViewEx
     */
    public void onNavigationItemSelected(final BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_add:
                        Intent newIntent = new Intent(CustomerListActivity.this, NewConnectionActivity.class);
                        newIntent.putExtra("areaName", getAreaName());
                        startActivity(newIntent);
                        break;

                    case R.id.ic_edit:
                        Intent editIntent = new Intent(CustomerListActivity.this, EditConnectionActivity.class);
                        editIntent.putExtra("areaName",getAreaName());
                        startActivity(editIntent);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * sets up view pager
     */
    private void setupViewPager() {
        Bundle bundle = new Bundle();
        bundle.putString("areaName", getAreaName());

        android.support.v4.app.Fragment connectionList = FragmentConnectionList.newInstance();
        connectionList.setArguments(bundle);

        android.support.v4.app.Fragment pendingList = FragmentPendingList.newInstance();
        pendingList.setArguments(bundle);

        android.support.v4.app.Fragment paidList = FragmentPaidList.newInstance();
        paidList.setArguments(bundle);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.addFragments(connectionList, getAreaName());
        sectionPagerAdapter.addFragments(pendingList, getAreaName());
        sectionPagerAdapter.addFragments(paidList, getAreaName());

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.toolbarTabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Connection List");
        tabLayout.getTabAt(1).setText("Pending List");
        tabLayout.getTabAt(2).setText("Paid List");
    }

    /**
     * This method copies value from the connectionList node and creates the new Node On First day of the month
     *
     * @param fromPath
     * @param toPath
     */
    public void copyConnectionList(DatabaseReference fromPath, final DatabaseReference toPath) {
        final Bundle bundle = new Bundle();
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            bundle.putString("CopyNode", "Error While Copying");
                            mFirebaseAnalytics.logEvent("Copying", bundle);
                            Log.d(TAG, "onComplete: Error while Copying");
                        } else {
                            bundle.putString("CopyNode", "Copy Successful");
                            mFirebaseAnalytics.logEvent("Copying", bundle);
                            Log.d(TAG, "onComplete: Copy Successful");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthListener();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    copyConnectionList(nodes.getNodeConnectionList().child(getAreaName()), nodes.getNodePendingList().child(getAreaName()).child(dateSetter.monthAndYear()));
                    copyConnectionList(nodes.getNodeConnectionList().child(getAreaName()), nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthListner();
    }
}
