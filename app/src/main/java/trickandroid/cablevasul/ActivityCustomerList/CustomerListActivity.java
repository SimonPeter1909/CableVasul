package trickandroid.cablevasul.ActivityCustomerList;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentConnectionList;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentPaidList;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments.FragmentPendingList;
import trickandroid.cablevasul.ActivityCustomerList.Details.NewConnectionDetails;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.FirebasePackage.InitializeFirebaseAuth;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;
import trickandroid.cablevasul.Utils.SectionPagerAdapter;
import trickandroid.cablevasul.Utils.ShowSnackBar;

public class CustomerListActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "CustomerListActivity";

    private TextView newConnectionDateTV, newConnectionAreaTV;

    //firebase
    private InitializeFirebaseAuth auth = new InitializeFirebaseAuth();
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    //utils
    private ShowSnackBar snackBar = new ShowSnackBar();
    private DateSetter dateSetter = new DateSetter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        setToolbar();
        auth.initializeFBAuth();
        setupViewPager();
        fabClick();

    }

    /**
     * gets area name from AreaActivity and stores as String
     * @return
     */
    public String getAreaName(){
        Intent intent = getIntent();
        return intent.getStringExtra("areaName");
    }

    /**
     * sets up toolbar
     */
    public void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getAreaName());
        toolbar.setSubtitle(dateSetter.ddmmyyyyday());
        setSupportActionBar(toolbar);
    }

    /**
     * sets up view pager
     */
    private void setupViewPager(){
        Bundle bundle = new Bundle();
        bundle.putString("areaName",getAreaName());

        android.support.v4.app.Fragment connectionList = FragmentConnectionList.newInstance();
        connectionList.setArguments(bundle);

        android.support.v4.app.Fragment pendingList = FragmentPendingList.newInstance();
        pendingList.setArguments(bundle);

        android.support.v4.app.Fragment paidList = FragmentPaidList.newInstance();
        paidList.setArguments(bundle);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.addFragments(connectionList,getAreaName());
        sectionPagerAdapter.addFragments(pendingList,getAreaName());
        sectionPagerAdapter.addFragments(paidList,getAreaName());

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.toolbarTabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Connection List");
        tabLayout.getTabAt(1).setText("Pending List");
        tabLayout.getTabAt(2).setText("Paid List");
    }

    /**
     * onClick action for FAB click
     */
    public void fabClick(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MaterialDialog mainDialog = new MaterialDialog.Builder(CustomerListActivity.this)
                        .title("Add new Connection")
                        .customView(R.layout.dialog_add_new_connection, true)

                        .build();

                final View newView = mainDialog.getView();
                onClickAdd(newView, mainDialog);

                mainDialog.show();
            }
        });
    }

    /**
     * method called inside fabClick
     * @param newView
     * @param maindialog
     */
    public void onClickAdd(final View newView, final MaterialDialog maindialog){
        newConnectionDateTV = newView.findViewById(R.id.newConnectionDateTV);
        newConnectionAreaTV = newView.findViewById(R.id.newConnectionAreaTV);
        newConnectionAreaTV.setText(getAreaName());
        newConnectionDateTV.setText(dateSetter.ddmmyyyy());
        Button addBtn = newView.findViewById(R.id.addBTN);
        Button cancelBtn = newView.findViewById(R.id.cancelBtn);
        final CheckBox paidCB = newView.findViewById(R.id.paidCheckBox);

        newConnectionDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(newView);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maindialog.cancel();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (nameString(newView).isEmpty()){
                    snackBar.snackBar(view,"Enter Name");
                } else if (mobileNumberString(newView).isEmpty()){
                    snackBar.snackBar(view,"Enter Mobile Number");
                } else if (connectionNumberString(newView).isEmpty()){
                    snackBar.snackBar(view,"Enter Connection Number");
                } else if (monthlyAmountString(newView).isEmpty()){
                    snackBar.snackBar(view,"Enter Monthly Amount");
                } else {
                    nodes.getNodeCheckConnectionNumber().child(getAreaName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()){
                                newConnectionsDetails(newView, view, maindialog, paidCB);
                            } else {
                                if (dataSnapshot.hasChild(connectionNumberString(newView))){
                                    snackBar.snackBar(view,"Connection Number Already Exists");
                                } else {
                                    newConnectionsDetails(newView, view, maindialog,paidCB);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    /**
     * displays datePicker onClick connectionDateTextView
     * @param view
     */
    public void showDatePicker(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                view.getContext(),
                CustomerListActivity.this,
                dateSetter.intyear,
                dateSetter.intmonth,
                dateSetter.intdate
        );
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    /**
     * adds value to the database when new Connection is added, this is called inside onClickAdd
     * @param newView
     * @param view
     * @param mainDialog
     */
    public void newConnectionsDetails(View newView, View view, MaterialDialog mainDialog,CheckBox paidCB){
        String paid = "Unpaid";
        nodes.getNodeCheckConnectionNumber().child(getAreaName()).child(connectionNumberString(newView)).setValue(connectionNumberString(newView));

        if (paidCB.isChecked()){
            paid = "Paid";
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(dateSetter.ddmmyyyyday(),nameString(newView), getAreaName(), monthlyAmountString(newView),connectionNumberString(newView),mobileNumberString(newView),aadharNumberString(newView),cafNumberString(newView),setUpBoxSerialNumberString(newView),paid);
            nodes.getNodeConnectionList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
            nodes.getNodePaidList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
        } else {
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(dateSetter.ddmmyyyyday(),nameString(newView), getAreaName(), monthlyAmountString(newView),connectionNumberString(newView),mobileNumberString(newView),aadharNumberString(newView),cafNumberString(newView),setUpBoxSerialNumberString(newView),paid);
            nodes.getNodeConnectionList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
            nodes.getNodePendingList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
        }

        setTotalAmount(newView, paidCB);

        editValuePerArea(paidCB);

        Toast.makeText(getApplicationContext(),"'"+connectionNumberString(newView)+"', '"+nameString(newView)+"' Successfully Added to Area '" + getAreaName() + "'",Toast.LENGTH_SHORT).show();
        snackBar.snackBar(view, "'"+connectionNumberString(newView)+"', '"+nameString(newView)+"' Successfully Added to Area '" + getAreaName() + "'");

        mainDialog.cancel();
    }

    /**
     * Changes the value of overall connections of the user, method called inside newConnectionsDetails
     * @param view
     */
    public void setTotalAmount(final View view, final CheckBox paidCB) {
        nodes.getNodeTotalAmount().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalAmount = dataSnapshot.child("TotalAmount").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Total Amount = " + String.valueOf(totalAmount));

                nodes.getNodeTotalAmount().child("TotalAmount").setValue(totalAmount+intMonthlyAmount(view));
                nodes.getNodeVasulList().child("totalAmount").setValue(totalAmount+intMonthlyAmount(view));
                nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).child("totalAmount").setValue(totalAmount+intMonthlyAmount(view));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nodes.getNodeTotalConnections().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int intTotal = dataSnapshot.child("TotalConnections").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Total Connections = " + String.valueOf(intTotal));

                nodes.getNodeTotalConnections().child("TotalConnections").setValue(intTotal+1);
                nodes.getNodeVasulList().child("totalConnections").setValue(intTotal+1);
                nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).child("totalConnections").setValue(intTotal+1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nodes.getNodeAmountCollected().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int amountCollected = dataSnapshot.child("AmountCollected").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Amount Collected = " + String.valueOf(amountCollected));

                if (paidCB.isChecked()){
                    nodes.getNodeAmountCollected().child("AmountCollected").setValue(amountCollected + intMonthlyAmount(view));
                    nodes.getNodeVasulList().child("amountCollected").setValue(amountCollected + intMonthlyAmount(view));
                    nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).child("amountCollected").setValue(amountCollected + intMonthlyAmount(view));
                } else {
                    nodes.getNodeAmountCollected().child("AmountCollected").setValue(amountCollected);
                    nodes.getNodeVasulList().child("amountCollected").setValue(amountCollected);
                    nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).child("amountCollected").setValue(amountCollected);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nodes.getNodePendingConnections().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pendingConnections = dataSnapshot.child("PendingConnections").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Pending Connections = " + String.valueOf(pendingConnections));

                if (paidCB.isChecked()){
                    nodes.getNodePendingConnections().child("PendingConnections").setValue(pendingConnections);
                    nodes.getNodeVasulList().child("pendingConnections").setValue(pendingConnections);
                    nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).child("pendingConnections").setValue(pendingConnections);
                } else {
                    nodes.getNodePendingConnections().child("PendingConnections").setValue(pendingConnections+1);
                    nodes.getNodeVasulList().child("pendingConnections").setValue(pendingConnections+1);
                    nodes.getNodeMonthDetails().child(dateSetter.wordMonth()+","+dateSetter.year()).child("pendingConnections").setValue(pendingConnections+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * edits the values displays in the areaList RecyclerView
     * @param paidCB
     */
    public void editValuePerArea(final CheckBox paidCB){
        nodes.getNodeAreaDetails().child(getAreaName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalConnection = dataSnapshot.child("totalConnections").getValue(Integer.class);
                int pendingConnection = dataSnapshot.child("pendingConnections").getValue(Integer.class);
                Log.d(TAG, "onDataChange: total connection per area = " + String.valueOf(totalConnection));

                if (paidCB.isChecked()){
                    nodes.getNodeAreaDetails().child(getAreaName()).child("totalConnections").setValue(totalConnection+1);
                    nodes.getNodeAreaDetails().child(getAreaName()).child("pendingConnections").setValue(pendingConnection);
                } else {
                    nodes.getNodeAreaDetails().child(getAreaName()).child("totalConnections").setValue(totalConnection+1);
                    nodes.getNodeAreaDetails().child(getAreaName()).child("pendingConnections").setValue(pendingConnection+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * returns the integer value entered in the AmountET
     * @param view
     * @return
     */
    public int intMonthlyAmount(View view){
        return Integer.parseInt(monthlyAmountString(view));
    }

    /**
     * gets key from nodeConnectionList
     * @return
     */
    public String getKey(){
        Log.d(TAG, "getKey: pushKey = " + nodes.getNodeCheckConnectionNumber().push().getKey());
        return nodes.getNodeCheckConnectionNumber().push().getKey();
    }

    /**
     * returns String value entered in the nameET
     * @param view
     * @return
     */
    public String nameString(View view){
        EditText nameET = view.findViewById(R.id.nameET);
        return nameET.getText().toString();
    }

    /**
     * returns String value entered in the mobileNumberET
     * @param view
     * @return
     */
    public String mobileNumberString(View view){
        EditText editText = view.findViewById(R.id.mobileNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the connectionNumberET
     * @param view
     * @return
     */
    public String connectionNumberString(View view){
        EditText editText = view.findViewById(R.id.connectionNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the monthlyAmountET
     * @param view
     * @return
     */
    public String monthlyAmountString(View view){
        EditText editText = view.findViewById(R.id.monthlyAmountET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the aadharNumberET
     * @param view
     * @return
     */
    public String aadharNumberString(View view){
        EditText editText = view.findViewById(R.id.aadharNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the cafET
     * @param view
     * @return
     */
    public String cafNumberString(View view){
        EditText editText = view.findViewById(R.id.cafET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the setUpSerialET
     * @param view
     * @return
     */
    public String setUpBoxSerialNumberString(View view){
        EditText editText = view.findViewById(R.id.setUpSerialET);
        return editText.getText().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthListner();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateFormat = String.format("%02d/%02d/%04d",dayOfMonth,month+1,year);
        newConnectionDateTV.setText(dateFormat);
    }
}
