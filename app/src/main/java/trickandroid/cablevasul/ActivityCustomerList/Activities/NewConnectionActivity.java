package trickandroid.cablevasul.ActivityCustomerList.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

import trickandroid.cablevasul.ActivityArea.Details.AreaDetails;
import trickandroid.cablevasul.ActivityCustomerList.Details.NewConnectionDetails;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.FirebasePackage.InitializeFirebaseAuth;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.CheckFirebaseCharecters;
import trickandroid.cablevasul.Utils.DateSetter;
import trickandroid.cablevasul.Utils.ShowSnackBar;

public class NewConnectionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "NewConnectionActivity";

    //widgets
    private TextView newConnectionDateTV;
    private Button addBtn, cancelBtn;
    private CheckBox paidCB;
    private SweetAlertDialog progressDialog;

    //utils
    private DateSetter dateSetter = new DateSetter();
    private ShowSnackBar snackBar = new ShowSnackBar();
    private CheckFirebaseCharecters checkFirebaseCharecters = new CheckFirebaseCharecters();

    ///firebase
    private InitializeFirebaseAuth auth = new InitializeFirebaseAuth();
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connection);
        auth.initializeFBAuth();
        setUpToolbar();
        initializeWidgets();
        openDatePickerDialog(newConnectionDateTV);
        onClickAddBtn();
        onClickCancelBtn();
    }

    /**
     * setUp Toolbar
     */
    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarNewConnection);
        toolbar.setTitle("Add New Connection");
        toolbar.setSubtitle("AreaName : " + getAreaName());
        toolbar.setNavigationIcon(R.drawable.ic_add_white);
        setSupportActionBar(toolbar);
    }

    /**
     * initialize widgets
     */
    private void initializeWidgets() {
        newConnectionDateTV = findViewById(R.id.newConnectionDateTV);
        newConnectionDateTV.setText(dateSetter.ddmmyyyy());
        addBtn = findViewById(R.id.addBTN);
        cancelBtn = findViewById(R.id.cancelBtn);
        paidCB = findViewById(R.id.paidCheckBox);
    }

    /**
     * onClick cancel button
     */
    private void onClickCancelBtn() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * on click add button
     */
    private void onClickAddBtn() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (connectionNumberString().isEmpty() || connectionNumberString().length() < 1 || connectionNumberString().length() > 5) {
                    displayErrorDialog("Enter Connection Number", "Connection Number Should Contain 1-5 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (monthlyAmountString().isEmpty() || monthlyAmountString().length() < 3 || monthlyAmountString().length() > 3) {
                    displayErrorDialog("Enter Monthly Amount", "Monthly Amount Should Contain 3 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (nameString().isEmpty() || nameString().length() < 1 || nameString().length() > 20) {
                    displayErrorDialog("Enter Name", "Name Should Contain 1-20 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (mobileNumberString().isEmpty() || mobileNumberString().length() < 10 || mobileNumberString().length() > 10) {
                    displayErrorDialog("Enter Mobile Number", "Mobile Number Should Contain 10 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (aadharNumberString().isEmpty() || aadharNumberString().length() < 12 || aadharNumberString().length() > 12) {
                    displayErrorDialog("Enter Aathar Number", "Aathar Number Should Contain 12 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (cafNumberString().isEmpty() || cafNumberString().length() < 7 || cafNumberString().length() > 7) {
                    displayErrorDialog("Enter CAF Number", "CAF Number Should Contain 7 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (setUpBoxSerialNumberString().isEmpty() || setUpBoxSerialNumberString().length() < 8 || setUpBoxSerialNumberString().length() > 8) {
                    displayErrorDialog("Enter SetTop Box Serial Number", "SetTop Box Serial Should Contain 8 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (!checkFirebaseCharecters.checkFirebaseCharecters(connectionNumberString())) {
                    displayErrorDialog("Error", "Connection Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (!checkFirebaseCharecters.checkFirebaseCharecters(monthlyAmountString())) {
                    displayErrorDialog("Error", "Monthly Amount Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (!checkFirebaseCharecters.checkFirebaseCharecters(nameString())) {
                    displayErrorDialog("Error", "Name Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (!checkFirebaseCharecters.checkFirebaseCharecters(mobileNumberString())) {
                    displayErrorDialog("Error", "Mobile Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (!checkFirebaseCharecters.checkFirebaseCharecters(aadharNumberString())) {
                    displayErrorDialog("Error", "Aathar Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (!checkFirebaseCharecters.checkFirebaseCharecters(cafNumberString())) {
                    displayErrorDialog("Error", "CAF Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else if (!checkFirebaseCharecters.checkFirebaseCharecters(setUpBoxSerialNumberString())) {
                    displayErrorDialog("Error", "SetUp Box Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else {
                    nodes.getNodeCheckConnectionNumber().child(getAreaName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                progressDialog = new SweetAlertDialog(NewConnectionActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                                        .setTitleText("Adding")
                                        .setContentText("Please Wait!!!");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                newConnectionsDetails(view, paidCB);
                            } else {
                                if (dataSnapshot.hasChild(connectionNumberString())) {
                                    snackBar.snackBar(view, "Connection Number Already Exists");
                                } else {
                                    progressDialog = new SweetAlertDialog(NewConnectionActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                                            .setTitleText("Adding")
                                            .setContentText("Please Wait!!!");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    newConnectionsDetails(view, paidCB);
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
     *
     * @param title
     * @param content
     * @return
     */
    public SweetAlertDialog displayErrorDialog(String title, String content) {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * adds value to the database when new Connection is added, this is called inside onClickAdd
     *
     * @param view
     */
    public void newConnectionsDetails(View view, CheckBox paidCB) {
        String paid = "Unpaid";

        //inserts connection number value in connectionNumberCheck Node
        nodes.getNodeCheckConnectionNumber().child(getAreaName()).child(connectionNumberString()).setValue(connectionNumberString());

        if (paidCB.isChecked()) {
            paid = "Paid";

            //adds value to the connectionListPerMonth node and PaidList node , with month node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(), nameString(), getAreaName(), monthlyAmountString(), connectionNumberString(), mobileNumberString(), aadharNumberString(), cafNumberString(), setUpBoxSerialNumberString(), dateSetter.ddmmyyyy(), paid, stringMonthAndYear(), intDate());
            nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString()).setValue(newConnectionDetails);
            nodes.getNodePaidList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString()).setValue(newConnectionDetails);

            //adds value to the connectionList node, without month node, String paid = UnPaid
            NewConnectionDetails newConnectionDetailsUnpaid = new NewConnectionDetails(newConnectionDateTV.getText().toString(), nameString(), getAreaName(), monthlyAmountString(), connectionNumberString(), mobileNumberString(), aadharNumberString(), cafNumberString(), setUpBoxSerialNumberString(), "Unpaid", "Unpaid", stringMonthAndYear(), intDate());
            nodes.getNodeConnectionList().child(getAreaName()).child(connectionNumberString()).setValue(newConnectionDetailsUnpaid);
        } else {

            //adds value to the connectionListPerMonth node and PendingList and connectionList node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(), nameString(), getAreaName(), monthlyAmountString(), connectionNumberString(), mobileNumberString(), aadharNumberString(), cafNumberString(), setUpBoxSerialNumberString(), paid, paid, stringMonthAndYear(), intDate());
            nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString()).setValue(newConnectionDetails);
            nodes.getNodePendingList().child(getAreaName()).child(dateSetter.monthAndYear()).child(connectionNumberString()).setValue(newConnectionDetails);
            nodes.getNodeConnectionList().child(getAreaName()).child(connectionNumberString()).setValue(newConnectionDetails);
        }

        setTotalAmount(paidCB);

        editValuePerArea(paidCB);

        setDailyList(paidCB);

        Toast.makeText(getApplicationContext(), "'" + connectionNumberString() + "', '" + nameString() + "' Successfully Added to Area '" + getAreaName() + "'", Toast.LENGTH_SHORT).show();
        snackBar.snackBar(view, "'" + connectionNumberString() + "', '" + nameString() + "' Successfully Added to Area '" + getAreaName() + "'");

        progressDialog.dismissWithAnimation();
        showAgainDialog();
    }

    /**
     * after adding a connection
     */
    private void showAgainDialog() {
        SweetAlertDialog againDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Connection " + connectionNumberString() + " Added SuccessFully")
                .setContentText("Do You Want To Add Another Connection?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                });
        againDialog.setCancelable(false);
        againDialog.show();
    }

    /**
     * set values of the dailyListNode
     *
     * @param paidCB
     */
    public void setDailyList(final CheckBox paidCB) {
        final String date = newConnectionDateTV.getText().toString().replace("/", ",");
        String paid = "Unpaid";
        if (paidCB.isChecked()) {
            paid = "Paid";
            //adds value to the connectionListPerMonth node and PaidList node , with month node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(), nameString(), getAreaName(), monthlyAmountString(), connectionNumberString(), mobileNumberString(), aadharNumberString(), cafNumberString(), setUpBoxSerialNumberString(), dateSetter.ddmmyyyy(), paid, stringMonthAndYear(), intDate());
            nodes.getNodeDailyList().child(date).child("Connection List").child(getAreaName()).child("paidList").child(connectionNumberString()).setValue(newConnectionDetails);
        } else {
            //adds value to the connectionListPerMonth node and PendingList and connectionList node
            NewConnectionDetails newConnectionDetails = new NewConnectionDetails(newConnectionDateTV.getText().toString(), nameString(), getAreaName(), monthlyAmountString(), connectionNumberString(), mobileNumberString(), aadharNumberString(), cafNumberString(), setUpBoxSerialNumberString(), paid, paid, stringMonthAndYear(), intDate());
            nodes.getNodeDailyList().child(date).child("Connection List").child(getAreaName()).child("pending list").child(connectionNumberString()).setValue(newConnectionDetails);
        }

        //DailyListNode for notification
        nodes.getNodeDailyList().child(date).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() && !paidCB.isChecked()) {
                    //Since AreaDetails Class contains the same Parameters, it is used
                    AreaDetails areaDetails = new AreaDetails(date, 1, 1);
                    nodes.getNodeDailyList().child(date).child("notification").setValue(areaDetails);
                } else if (!dataSnapshot.exists() && paidCB.isChecked()) {
                    //Since AreaDetails Class contains the same Parameters, it is used
                    AreaDetails areaDetails = new AreaDetails(date, 1, 0);
                    nodes.getNodeDailyList().child(date).child("notification").setValue(areaDetails);
                } else {

                    int totalConnection = dataSnapshot.child("totalConnections").getValue(Integer.class);
                    int pendingConnection = dataSnapshot.child("pendingConnections").getValue(Integer.class);
                    Log.d(TAG, "onDataChange: total connection per area = " + String.valueOf(totalConnection));

                    if (paidCB.isChecked()) {
                        nodes.getNodeDailyList().child(date).child("notification").child("totalConnections").setValue(totalConnection + 1);
                        nodes.getNodeDailyList().child(date).child("notification").child("pendingConnections").setValue(pendingConnection);
                    } else {
                        nodes.getNodeDailyList().child(date).child("notification").child("totalConnections").setValue(totalConnection + 1);
                        nodes.getNodeDailyList().child(date).child("notification").child("pendingConnections").setValue(pendingConnection + 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //dailyList - dateList node
        nodes.getNodeDailyList().child("dateList").child(date).setValue(date);

        //DailyListNode for areaName
        nodes.getNodeDailyList().child(date).child(getAreaName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() && !paidCB.isChecked()) {
                    //Since AreaDetails Class contains the same Parameters, it is used
                    AreaDetails areaDetails = new AreaDetails(date, 1, 1);
                    nodes.getNodeDailyList().child(date).child(getAreaName()).setValue(areaDetails);
                } else if (!dataSnapshot.exists() && paidCB.isChecked()) {
                    //Since AreaDetails Class contains the same Parameters, it is used
                    AreaDetails areaDetails = new AreaDetails(date, 1, 0);
                    nodes.getNodeDailyList().child(date).child(getAreaName()).setValue(areaDetails);
                } else {
                    int totalConnection = dataSnapshot.child("totalConnections").getValue(Integer.class);
                    int pendingConnection = dataSnapshot.child("pendingConnections").getValue(Integer.class);
                    Log.d(TAG, "onDataChange: total connection per area = " + String.valueOf(totalConnection));

                    if (paidCB.isChecked()) {
                        nodes.getNodeDailyList().child(date).child(getAreaName()).child("totalConnections").setValue(totalConnection + 1);
                        nodes.getNodeDailyList().child(date).child(getAreaName()).child("pendingConnections").setValue(pendingConnection);
                    } else {
                        nodes.getNodeDailyList().child(date).child(getAreaName()).child("totalConnections").setValue(totalConnection + 1);
                        nodes.getNodeDailyList().child(date).child(getAreaName()).child("pendingConnections").setValue(pendingConnection + 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Changes the value of overall connections of the user, method called inside newConnectionsDetails
     */
    public void setTotalAmount(final CheckBox paidCB) {

        //TotalAmount
        nodes.getNodeTotalAmount().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalAmount = dataSnapshot.child("TotalAmount").getValue(Integer.class);
                Log.d(TAG, "onDataChange: Total Amount = " + String.valueOf(totalAmount));

                nodes.getNodeTotalAmount().child("TotalAmount").setValue(totalAmount + intMonthlyAmount());
                nodes.getNodeVasulList().child("totalAmount").setValue(totalAmount + intMonthlyAmount());
                nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("totalAmount").setValue(totalAmount + intMonthlyAmount());

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

                nodes.getNodeTotalConnections().child("TotalConnections").setValue(intTotal + 1);
                nodes.getNodeVasulList().child("totalConnections").setValue(intTotal + 1);
                nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("totalConnections").setValue(intTotal + 1);
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

                if (paidCB.isChecked()) {
                    nodes.getNodeAmountCollected().child("AmountCollected").setValue(amountCollected + intMonthlyAmount());
                    nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("amountCollected").setValue(amountCollected + intMonthlyAmount());
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

                if (paidCB.isChecked()) {
                    nodes.getNodePendingConnections().child("PendingConnections").setValue(pendingConnections);
                    nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("pendingConnections").setValue(pendingConnections);
                } else {
                    nodes.getNodePendingConnections().child("PendingConnections").setValue(pendingConnections + 1);
                    nodes.getNodeMonthDetails().child(dateSetter.monthAndYear()).child("pendingConnections").setValue(pendingConnections + 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * edits the values displays in the areaList RecyclerView
     *
     * @param paidCB
     */
    public void editValuePerArea(final CheckBox paidCB) {
        nodes.getNodeAreaDetails().child(getAreaName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalConnection = dataSnapshot.child("totalConnections").getValue(Integer.class);
                int pendingConnection = dataSnapshot.child("pendingConnections").getValue(Integer.class);
                Log.d(TAG, "onDataChange: total connection per area = " + String.valueOf(totalConnection));

                if (paidCB.isChecked()) {
                    nodes.getNodeAreaDetails().child(getAreaName()).child("totalConnections").setValue(totalConnection + 1);
                    nodes.getNodeAreaDetails().child(getAreaName()).child("pendingConnections").setValue(pendingConnection);
                } else {
                    nodes.getNodeAreaDetails().child(getAreaName()).child("totalConnections").setValue(totalConnection + 1);
                    nodes.getNodeAreaDetails().child(getAreaName()).child("pendingConnections").setValue(pendingConnection + 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * get the value from the newConnectionDateTV, splits and returns monthAndYear String
     *
     * @return
     */
    public String stringMonthAndYear() {
        String dateFormTV = newConnectionDateTV.getText().toString();
        String dateArray[] = dateFormTV.split("/");
        return dateSetter.wordMonth(Integer.parseInt(dateArray[1])) + "," + dateArray[2];
    }

    /**
     * get the value from the newConnectionDateTV, splits and returns intDate
     *
     * @return
     */
    public int intDate() {
        String dateFormTV = newConnectionDateTV.getText().toString();
        String dateArray[] = dateFormTV.split("/");
        return Integer.parseInt(dateArray[0]);
    }

    /**
     * to open date picker dialog when the textView is clicked
     *
     * @param textView
     */
    private void openDatePickerDialog(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    /**
     * displays datePicker onClick connectionDateTextView
     */
    public void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                NewConnectionActivity.this,
                NewConnectionActivity.this,
                dateSetter.intyear,
                dateSetter.intmonth,
                dateSetter.intdate
        );
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
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
     * returns the integer value entered in the AmountET
     *
     * @return
     */
    public int intMonthlyAmount() {
        return Integer.parseInt(monthlyAmountString());
    }

    /**
     * returns String value entered in the nameET
     *
     * @return
     */
    public String nameString() {
        MaterialEditText nameET = findViewById(R.id.nameET);
        return nameET.getText().toString();
    }

    /**
     * returns String value entered in the mobileNumberET
     *
     * @return
     */
    public String mobileNumberString() {
        MaterialEditText editText = findViewById(R.id.mobileNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the connectionNumberET
     *
     * @return
     */
    public String connectionNumberString() {
        MaterialEditText editText = findViewById(R.id.connectionNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the monthlyAmountET
     *
     * @return
     */
    public String monthlyAmountString() {
        MaterialEditText editText = findViewById(R.id.monthlyAmountET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the aadharNumberET
     *
     * @return
     */
    public String aadharNumberString() {
        MaterialEditText editText = findViewById(R.id.aadharNumberET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the cafET
     *
     * @return
     */
    public String cafNumberString() {
        MaterialEditText editText = findViewById(R.id.cafET);
        return editText.getText().toString();
    }

    /**
     * returns String value entered in the setUpSerialET
     *
     * @return
     */
    public String setUpBoxSerialNumberString() {
        MaterialEditText editText = findViewById(R.id.setUpSerialET);
        return editText.getText().toString();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateFormat = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
        newConnectionDateTV.setText(dateFormat);
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
}
