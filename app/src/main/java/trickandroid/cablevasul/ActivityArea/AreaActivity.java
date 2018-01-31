package trickandroid.cablevasul.ActivityArea;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trickandroid.cablevasul.ActivityArea.AreaFragments.FragmentAreaList;
import trickandroid.cablevasul.ActivityArea.AreaFragments.FragmentMonthList;
import trickandroid.cablevasul.ActivityArea.Details.AreaDetails;
import trickandroid.cablevasul.ActivityArea.Details.MonthDetails;
import trickandroid.cablevasul.ActivityArea.Details.VasulDetails;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.FirebasePackage.InitializeFirebaseAuth;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;
import trickandroid.cablevasul.Utils.SectionPagerAdapter;
import trickandroid.cablevasul.Utils.ShowSnackBar;

public class AreaActivity extends AppCompatActivity {
    private static final String TAG = "AreaActivity";

    //variables
    private int intTotalConnections = 0;
    private int intPendingConnections = 0;
    private int intTotalAmount = 0;
    private int intAmountCollected = 0;

    //widgets
    private Toolbar toolbar;
    private RelativeLayout progressLayout;
    private Button fab;
    private TextView totalConnectionsTV, pendingTV, totalAmountTV, amountCollectedTV;


    //firebase
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private InitializeFirebaseAuth auth = new InitializeFirebaseAuth();

    //utils
    private ShowSnackBar snackBar = new ShowSnackBar();
    private DateSetter dateSetter = new DateSetter();

    //progressBar
    private MaterialDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        setSupportActionBar(toolbar);
        auth.initializeFBAuth();
        initializeWidgets();
        setProgressBar();
        setToolbar();
        setupViewPager();
        setVasulList();
        addMonth();
        setTotalAmount();
        setTotalConnections();
        fabClick();
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
     * initialize the values of the vasul list node
     */
    public void setVasulList(){
        nodes.getNodeVasulList().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    VasulDetails vasulDetails = new VasulDetails(0,0,0,0);
                    nodes.getNodeVasulList().setValue(vasulDetails);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * initialize Widgets
     */
    public void initializeWidgets(){
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        totalAmountTV = findViewById(R.id.totalAmountTV);
        amountCollectedTV = findViewById(R.id.amountCollectedTV);
        totalConnectionsTV = findViewById(R.id.totalConecTV);
        pendingTV = findViewById(R.id.pendingConnecTV);
    }

    /**
     * sets up toolbar
     */
    public void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(dateSetter.ddmmyyyyday());
        setSupportActionBar(toolbar);
    }

    /**
     * sets up view pager
     */
    private void setupViewPager(){

        Bundle bundle = new Bundle();
        bundle.putString("areaName","areaName");

        Fragment areaList = FragmentAreaList.newInstance();
        areaList.setArguments(bundle);

        Fragment monthList = FragmentMonthList.newInstance();
        monthList.setArguments(bundle);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.addFragments(areaList,"area");
        sectionPagerAdapter.addFragments(monthList,"area");

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.toolbarTabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Area List");
        tabLayout.getTabAt(1).setText("Month List");
    }

    /**
     * 1.opens MaterialDialog to add area
     * 2.checks for existing area, if area already exists displays SnackBar
     *          else adds new Area under AreaList and details at AreaDetails
     */
    public void fabClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new MaterialDialog.Builder(AreaActivity.this)
                        .title("Add Area")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .inputRange(5, 25, Color.RED)
                        .input("Area Name", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                final String area = String.valueOf(input);

                                nodes.getNodeAreaList().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            if (dataSnapshot.hasChild(area)) {
                                                snackBar.snackBar(view, "Area " + area + " Already Exists");

                                                Log.d(TAG, "onDataChange: Area " + area + " Already Exists");
                                            } else {
                                                addArea(area, view);
                                            }
                                        } else {
                                            addArea(area, view);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Adds area and areaDetails to firebase database
     * @param area
     * @param view
     */
    public void addArea(String area, View view) {
        //add child to areaList Node
        nodes.getNodeAreaList().child(area).setValue(area);

        //add Child to areaDetails Node
        AreaDetails areaDetails = new AreaDetails(area, 0, 0);
        nodes.getNodeAreaDetails().child(area).setValue(areaDetails);

        snackBar.snackBar(view, "Area " + area + " Added");

        Log.d(TAG, "onDataChange: area " + area + " Added");
    }

    /**
     * add month details to the Firebase Database
     * Adds new Month Node on every First day of the Month
     */
    public void addMonth(){
        nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                   nodes.getNodeVasulList().addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           if (!dataSnapshot.exists()){
                               MonthDetails monthDetails = new MonthDetails(dateSetter.wordMonth()+","+dateSetter.year(),0,0,0,0);
                               nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).setValue(monthDetails);
                           }else {
                               int totalAmount = dataSnapshot.child("totalAmount").getValue(Integer.class);
                               int totalConnections = dataSnapshot.child("totalConnections").getValue(Integer.class);
                               MonthDetails monthDetails = new MonthDetails(dateSetter.wordMonth()+","+dateSetter.year(),totalConnections,totalConnections,totalAmount,0);
                               nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).setValue(monthDetails);
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * if the app runs for the first time, creates a new node for Total amount collected and Amount
     * Collected
     * else reads the value in the getNodeTotalAmount node and getNodeAmountCollected
     */
    public void setTotalAmount(){
        nodes.getNodeTotalAmount().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    nodes.getNodeTotalAmount().child("TotalAmount").setValue(intTotalAmount);
                } else {
                    int totalAmount = dataSnapshot.child("TotalAmount").getValue(Integer.class);
                    Log.d(TAG, "onDataChange: Total Amount = " + String.valueOf(totalAmount));
                    totalAmountTV.setText(String.valueOf(totalAmount));
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nodes.getNodeAmountCollected().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    nodes.getNodeAmountCollected().child("AmountCollected").setValue(intAmountCollected);
                } else {
                    int amountCollected = dataSnapshot.child("AmountCollected").getValue(Integer.class);
                    Log.d(TAG, "onDataChange: Amount Collected = " + String.valueOf(amountCollected));
                    amountCollectedTV.setText(String.valueOf(amountCollected));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * if the app runs for the first time, creates a new node for Total Connections and Pending
     * Connections
     * else reads the value in the setToatalConnections and getNodePendingConnections node
     */
    public void setTotalConnections(){
        nodes.getNodeTotalConnections().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    nodes.getNodeTotalConnections().child("TotalConnections").setValue(intTotalConnections);
                } else {
                    int intTotal = dataSnapshot.child("TotalConnections").getValue(Integer.class);
                    Log.d(TAG, "onDataChange: Total Connections = " + String.valueOf(intTotal));
                    totalConnectionsTV.setText(String.valueOf(intTotal));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nodes.getNodePendingConnections().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    nodes.getNodePendingConnections().child("PendingConnections").setValue(intPendingConnections);
                } else {
                    int pendingConnections = dataSnapshot.child("PendingConnections").getValue(Integer.class);
                    Log.d(TAG, "onDataChange: Pending Connections = " + String.valueOf(pendingConnections));
                    pendingTV.setText(String.valueOf(pendingConnections));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * method to display toast Message
     * @param message
     */
    public void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
