package trickandroid.cablevasul.ActivityCustomerList;


import android.app.DatePickerDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

import trickandroid.cablevasul.ActivityArea.Details.AreaDetails;
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
     * Cancels progressBar after the firebase values are loaded
     */
    public void cancelProgressBar(){
        nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
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
    public void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: starting");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigationBar);
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bottomNavigationViewEx.setIconTintList(0,getColorStateList(R.color.Green));
            bottomNavigationViewEx.setIconTintList(1,getColorStateList(R.color.Brown));
            bottomNavigationViewEx.setIconTintList(2,getColorStateList(R.color.Blue));
            bottomNavigationViewEx.setIconTintList(3,getColorStateList(R.color.Red));
            bottomNavigationViewEx.setIconTintList(4,getColorStateList(R.color.DarkOrange));
            bottomNavigationViewEx.setTextTintList(0,getColorStateList(R.color.Green));
            bottomNavigationViewEx.setTextTintList(1,getColorStateList(R.color.Brown));
            bottomNavigationViewEx.setTextTintList(2,getColorStateList(R.color.Blue));
            bottomNavigationViewEx.setTextTintList(3,getColorStateList(R.color.Red));
            bottomNavigationViewEx.setTextTintList(4,getColorStateList(R.color.DarkOrange));
        }
        onNavigationItemSelected(bottomNavigationViewEx);
    }

    /**
     * displays corresponding Dialog Box when the Item is clicked
     * @param bottomNavigationViewEx
     */
    public void onNavigationItemSelected(final BottomNavigationViewEx bottomNavigationViewEx){
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_add:
                        displayAddDialog();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * onClick add item Displays Add new Connection Dialog
     */
    public void displayAddDialog(){
        MaterialDialog mainDialog = new MaterialDialog.Builder(CustomerListActivity.this)
                .title("Add new Connection")
                .titleColorRes(R.color.Blue)
                .iconRes(R.drawable.ic_add)
                .customView(R.layout.dialog_add_new_connection, true)
                .build();

        final View newView = mainDialog.getView();
        onClickAdd(newView, mainDialog);

        mainDialog.show();
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
                if (connectionNumberString(newView).isEmpty() || connectionNumberString(newView).length()<1 || connectionNumberString(newView).length()>5){
                    displayErrorDialog("Enter Connection Number", "Connection Number Should Contain 1-5 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else  if (monthlyAmountString(newView).isEmpty() || monthlyAmountString(newView).length()<3 || monthlyAmountString(newView).length()>3){
                    displayErrorDialog("Enter Monthly Amount", "Monthly Amount Should Contain 3 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (nameString(newView).isEmpty() || nameString(newView).length()<1 || nameString(newView).length()>20){
                    displayErrorDialog("Enter Name", "Name Should Contain 1-20 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (mobileNumberString(newView).isEmpty() || mobileNumberString(newView).length()<10 || mobileNumberString(newView).length()>10){
                    displayErrorDialog("Enter Mobile Number", "Mobile Number Should Contain 10 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (aadharNumberString(newView).isEmpty() || aadharNumberString(newView).length()<12 || aadharNumberString(newView).length()>12){
                    displayErrorDialog("Enter Aathar Number", "Aathar Number Should Contain 12 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (cafNumberString(newView).isEmpty() || cafNumberString(newView).length()<7 || cafNumberString(newView).length()>7){
                    displayErrorDialog("Enter CAF Number", "CAF Number Should Contain 7 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (setUpBoxSerialNumberString(newView).isEmpty() ||setUpBoxSerialNumberString(newView).length()<8 || setUpBoxSerialNumberString(newView).length()>8){
                    displayErrorDialog("Enter SetTop Box Serial Number", "SetTop Box Serial Should Contain 8 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else{
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
     * displays error dialog when editText fields are not up to the course
     * @param title
     * @param content
     * @return
     */
    public SweetAlertDialog displayErrorDialog(String title, String content){
        SweetAlertDialog dialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
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

        //inserts connection number value in connectionNumberCheck Node
        nodes.getNodeCheckConnectionNumber().child(getAreaName()).child(connectionNumberString(newView)).setValue(connectionNumberString(newView));

        if (paidCB.isChecked()){
            paid = "Paid";

            //adds value to the connectionListPerMonth node and PaidList node , with month node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(),nameString(newView), getAreaName(), monthlyAmountString(newView),connectionNumberString(newView),mobileNumberString(newView),aadharNumberString(newView),cafNumberString(newView),setUpBoxSerialNumberString(newView), dateSetter.ddmmyyyy(),paid,stringMonthAndYear(),intDate());
            nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
            nodes.getNodePaidList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);

            //adds value to the connectionList node, without month node, String paid = UnPaid
            NewConnectionDetails newConnectionDetailsUnpaid = new NewConnectionDetails(newConnectionDateTV.getText().toString(),nameString(newView), getAreaName(), monthlyAmountString(newView),connectionNumberString(newView),mobileNumberString(newView),aadharNumberString(newView),cafNumberString(newView),setUpBoxSerialNumberString(newView), "Unpaid","Unpaid",stringMonthAndYear(),intDate());
            nodes.getNodeConnectionList().child(getAreaName()).child(connectionNumberString(newView)).setValue(newConnectionDetailsUnpaid);
        } else {

            //adds value to the connectionListPerMonth node and PendingList and connectionList node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(),nameString(newView), getAreaName(), monthlyAmountString(newView),connectionNumberString(newView),mobileNumberString(newView),aadharNumberString(newView),cafNumberString(newView),setUpBoxSerialNumberString(newView),paid,paid,stringMonthAndYear(),intDate());
            nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
            nodes.getNodePendingList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
            nodes.getNodeConnectionList().child(getAreaName()).child(connectionNumberString(newView)).setValue(newConnectionDetails);
        }

        setTotalAmount(newView, paidCB);

        editValuePerArea(paidCB);

        setDailyList(paidCB, newView);

        Toast.makeText(getApplicationContext(),"'"+connectionNumberString(newView)+"', '"+nameString(newView)+"' Successfully Added to Area '" + getAreaName() + "'",Toast.LENGTH_SHORT).show();
        snackBar.snackBar(view, "'"+connectionNumberString(newView)+"', '"+nameString(newView)+"' Successfully Added to Area '" + getAreaName() + "'");

        mainDialog.cancel();
    }

    /**
     * get the value from the newConnectionDateTV, splits and returns monthAndYear String
     * @return
     */
    public String stringMonthAndYear(){
        String dateFormTV = newConnectionDateTV.getText().toString();
        String dateArray[] = dateFormTV.split("/");
        return dateSetter.wordMonth(Integer.parseInt(dateArray[1]))+","+dateArray[2];
    }

    /**
     *  get the value from the newConnectionDateTV, splits and returns intDate
     * @return
     */
    public int intDate(){
        String dateFormTV = newConnectionDateTV.getText().toString();
        String dateArray[] = dateFormTV.split("/");
        return Integer.parseInt(dateArray[0]);
    }

    public void setDailyList(final CheckBox paidCB, final View newView){
        final String date = newConnectionDateTV.getText().toString().replace("/",",");
        String paid = "Unpaid";
        if (paidCB.isChecked()){
            paid = "Paid";
            //adds value to the connectionListPerMonth node and PaidList node , with month node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(),nameString(newView), getAreaName(), monthlyAmountString(newView),connectionNumberString(newView),mobileNumberString(newView),aadharNumberString(newView),cafNumberString(newView),setUpBoxSerialNumberString(newView),dateSetter.ddmmyyyy(),paid,stringMonthAndYear(),intDate());
            nodes.getNodeDailyList().child(date).child("Connection List").child("paidList").child(connectionNumberString(newView)).setValue(newConnectionDetails);
        } else {
            //adds value to the connectionListPerMonth node and PendingList and connectionList node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(),nameString(newView), getAreaName(), monthlyAmountString(newView),connectionNumberString(newView),mobileNumberString(newView),aadharNumberString(newView),cafNumberString(newView),setUpBoxSerialNumberString(newView),paid,paid,stringMonthAndYear(),intDate());
            nodes.getNodeDailyList().child(date).child("Connection List").child("pending list").child(connectionNumberString(newView)).setValue(newConnectionDetails);
        }

        //DailyListNode for notification
        nodes.getNodeDailyList().child(date).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() && !paidCB.isChecked()){
                    //Since AreaDetails Class contains the same Parameters, it is used
                    AreaDetails areaDetails = new AreaDetails(date,1,1);
                    nodes.getNodeDailyList().child(date).child("notification").setValue(areaDetails);
                } else if (!dataSnapshot.exists() && paidCB.isChecked()){
                    //Since AreaDetails Class contains the same Parameters, it is used
                    AreaDetails areaDetails = new AreaDetails(date,1,0);
                    nodes.getNodeDailyList().child(date).child("notification").setValue(areaDetails);
                } else {

                    int totalConnection = dataSnapshot.child("totalConnections").getValue(Integer.class);
                    int pendingConnection = dataSnapshot.child("pendingConnections").getValue(Integer.class);
                    Log.d(TAG, "onDataChange: total connection per area = " + String.valueOf(totalConnection));

                    if (paidCB.isChecked()){
                        nodes.getNodeDailyList().child(date).child("notification").child("totalConnections").setValue(totalConnection+1);
                        nodes.getNodeDailyList().child(date).child("notification").child("pendingConnections").setValue(pendingConnection);
                    } else {
                        nodes.getNodeDailyList().child(date).child("notification").child("totalConnections").setValue(totalConnection+1);
                        nodes.getNodeDailyList().child(date).child("notification").child("pendingConnections").setValue(pendingConnection+1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //dailyList - dateList node
        nodes.getNodeDailyList().child("dateList").child(date).setValue(date);
    }

    /**
     * Changes the value of overall connections of the user, method called inside newConnectionsDetails
     * @param view
     */
    public void setTotalAmount(final View view, final CheckBox paidCB) {

        //TotalAmount
        nodes.getNodeTotalAmount().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalAmount = dataSnapshot.child("TotalAmount").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Total Amount = " + String.valueOf(totalAmount));

                nodes.getNodeTotalAmount().child("TotalAmount").setValue(totalAmount+intMonthlyAmount(view));
                nodes.getNodeVasulList().child("totalAmount").setValue(totalAmount+intMonthlyAmount(view));
                nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("totalAmount").setValue(totalAmount+intMonthlyAmount(view));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TotalConnections
        nodes.getNodeTotalConnections().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int intTotal = dataSnapshot.child("TotalConnections").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Total Connections = " + String.valueOf(intTotal));

                nodes.getNodeTotalConnections().child("TotalConnections").setValue(intTotal+1);
                nodes.getNodeVasulList().child("totalConnections").setValue(intTotal+1);
                nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("totalConnections").setValue(intTotal+1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //AmountCollected
        nodes.getNodeAmountCollected().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int amountCollected = dataSnapshot.child("AmountCollected").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Amount Collected = " + String.valueOf(amountCollected));

                if (paidCB.isChecked()){
                    nodes.getNodeAmountCollected().child("AmountCollected").setValue(amountCollected + intMonthlyAmount(view));
                    nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("amountCollected").setValue(amountCollected + intMonthlyAmount(view));
                } else {
                    nodes.getNodeAmountCollected().child("AmountCollected").setValue(amountCollected);
                    nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("amountCollected").setValue(amountCollected);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //PendingConnections
        nodes.getNodePendingConnections().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pendingConnections = dataSnapshot.child("PendingConnections").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Pending Connections = " + String.valueOf(pendingConnections));

                if (paidCB.isChecked()){
                    nodes.getNodePendingConnections().child("PendingConnections").setValue(pendingConnections);
                    nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("pendingConnections").setValue(pendingConnections);
                } else {
                    nodes.getNodePendingConnections().child("PendingConnections").setValue(pendingConnections+1);
                    nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("pendingConnections").setValue(pendingConnections+1);
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
        MaterialEditText nameET = view.findViewById(R.id.nameET);
        return nameET.getText().toString();
    }

    /**
     * returns String value entered in the mobileNumberET
     * @param view
     * @return
     */
    public String mobileNumberString(View view){
        MaterialEditText editText = view.findViewById(R.id.mobileNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the connectionNumberET
     * @param view
     * @return
     */
    public String connectionNumberString(View view){
        MaterialEditText editText = view.findViewById(R.id.connectionNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the monthlyAmountET
     * @param view
     * @return
     */
    public String monthlyAmountString(View view){
        MaterialEditText editText = view.findViewById(R.id.monthlyAmountET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the aadharNumberET
     * @param view
     * @return
     */
    public String aadharNumberString(View view){
        MaterialEditText editText = view.findViewById(R.id.aadharNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the cafET
     * @param view
     * @return
     */
    public String cafNumberString(View view){
        MaterialEditText editText = view.findViewById(R.id.cafET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the setUpSerialET
     * @param view
     * @return
     */
    public String setUpBoxSerialNumberString(View view){
        MaterialEditText editText = view.findViewById(R.id.setUpSerialET);
        return editText.getText().toString();
    }

    /**
     * This method copies value from the connectionList node and creates the new Node On First day of the month
     * @param fromPath
     * @param toPath
     */
    public void copyConnectionList(DatabaseReference fromPath, final DatabaseReference toPath){
        final Bundle bundle = new Bundle();
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            bundle.putString("CopyNode","Error While Copying");
                            mFirebaseAnalytics.logEvent("Copying",bundle);
                            Log.d(TAG, "onComplete: Error while Copying");
                        } else {
                            bundle.putString("CopyNode","Copy Successful");
                            mFirebaseAnalytics.logEvent("Copying",bundle);
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
                if (!dataSnapshot.exists()){
                    copyConnectionList(nodes.getNodeConnectionList().child(getAreaName()),nodes.getNodePendingList().child(getAreaName()).child(dateSetter.monthAndYear()));
                    copyConnectionList(nodes.getNodeConnectionList().child(getAreaName()),nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()));
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateFormat = String.format("%02d/%02d/%04d",dayOfMonth,month+1,year);
        newConnectionDateTV.setText(dateFormat);
    }
}
