package trickandroid.cablevasul.ActivityCustomerList.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.CheckFirebaseCharecters;
import trickandroid.cablevasul.Utils.MaterialProgressBar;

public class EditConnectionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EditConnectionActivity";

    //widgets
    private TextView dateTV, connectionNumberTV, areaTV;
    private MaterialEditText monthlyAmountET, nameET, mobileNumberET, aatharNumberET, cafET, setupBoxSerialET;
    private Button addBTN, cancelBTN;
    private CheckBox paidCB;

    //utils
    private MaterialProgressBar materialProgressBar = new MaterialProgressBar();
    private CheckFirebaseCharecters checkFirebaseCharecters = new CheckFirebaseCharecters();

    //List
    private List<String> areaList = new ArrayList<>();
    private List<String> connectionNumberList = new ArrayList<>();

    //adapter
    private ArrayAdapter<String> connectionListAdapter;

    //firebase
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_connection);
        initializeWidgets();
        setUpToolbar();
        collectAreaList();
    }

    /**
     * initialize Widgets
     */
    private void initializeWidgets() {
        dateTV = findViewById(R.id.newConnectionDateTV);
        connectionNumberTV = findViewById(R.id.newConnectionNumberTV);
        areaTV = findViewById(R.id.areaTV);

        monthlyAmountET = findViewById(R.id.monthlyAmountET);
        nameET = findViewById(R.id.nameET);
        mobileNumberET = findViewById(R.id.mobileNumberET);
        aatharNumberET = findViewById(R.id.aadharNumberET);
        cafET = findViewById(R.id.cafET);
        setupBoxSerialET = findViewById(R.id.setUpSerialET);

        addBTN = findViewById(R.id.addBTN);
        addBTN.setOnClickListener(this);
        cancelBTN = findViewById(R.id.cancelBtn);
        cancelBTN.setOnClickListener(this);

        paidCB = findViewById(R.id.paidCheckBox);
    }

    /**
     * set up toolbar
     */
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * searches arealist node and add the values to areaList
     */
    private void collectAreaList() {
        materialProgressBar.startProgressBar(EditConnectionActivity.this);
        nodes.getNodeAreaList().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String area = areaSnapshot.getValue(String.class);
                    areaList.add(area);
                }
                collectAreaList(areaList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * on Select Area
     */
    private void collectAreaList(final List<String> areaList) {
        materialProgressBar.stopProgressBar();
        Log.d(TAG, "collectAreaList: progress bar dismissed");
        new MaterialDialog.Builder(EditConnectionActivity.this)
                .title("Select an Area")
                .items(areaList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        collectConnectionNumberList(String.valueOf(text));
                        areaTV.setText(String.valueOf(text));
                        areaList.clear();
                    }
                })
                .show();
        areaList.clear();
    }

    /**
     * collects data of connectionNumberList
     *
     * @param area
     */
    private void collectConnectionNumberList(final String area) {
        materialProgressBar.startProgressBar(EditConnectionActivity.this);
        nodes.getNodeCheckConnectionNumber().child(area).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String connectionNumber = dataSnapshot1.getValue(String.class);
                    Log.d(TAG, "onDataChange: connection Number = " + connectionNumber);
                    connectionNumberList.add(connectionNumber);
                }
                openSearchDialog(area, connectionNumberList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * opens searchConnectionNumber Dialog
     *
     * @param connectionNumberList
     */
    private void openSearchDialog(final String area, final List<String> connectionNumberList) {
        materialProgressBar.stopProgressBar();
        Log.d(TAG, "collectAreaList: progress bar dismissed");
        MaterialDialog.Builder builder = new MaterialDialog.Builder(EditConnectionActivity.this)
                .title("Search Connection Number")
                .customView(R.layout.dialog_search_connenction_number, false);

        final MaterialDialog dialog = builder.build();

        final View view = dialog.getCustomView();
        EditText searchET = view.findViewById(R.id.searchET);
        ListView connectionNumberLV = view.findViewById(R.id.connectionNumberLV);

        connectionListAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, connectionNumberList);
        Log.d(TAG, "openSearchDialog: adapter = " + connectionListAdapter);

        connectionNumberLV.setAdapter(connectionListAdapter);

        //onCLickListViewItem
        connectionNumberLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                materialProgressBar.startProgressBar(EditConnectionActivity.this);
                String connectionNumber = parent.getItemAtPosition(position).toString();

                getConnectionDetails(area, connectionNumber, dialog);
            }
        });

        //filter list
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                connectionListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * gets Connection details
     *
     * @param area
     * @param connectionNumber
     */
    private void getConnectionDetails(String area, String connectionNumber, final MaterialDialog dialog) {
        nodes.getNodeConnectionList().child(area).child(connectionNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String connectionDate = dataSnapshot.child("date").getValue(String.class);
                dateTV.setText(connectionDate);

                String connectionNumber = dataSnapshot.child("connectionNumber").getValue(String.class);
                connectionNumberTV.setText(connectionNumber);

                String monthlyAmount = dataSnapshot.child("monthlyAmount").getValue(String.class);
                monthlyAmountET.setText(monthlyAmount);
                Log.d(TAG, "onDataChange: oldMonthlyAmount = " + monthlyAmount);

                String name = dataSnapshot.child("name").getValue(String.class);
                nameET.setText(name);

                String mobileNumber = dataSnapshot.child("mobileNumber").getValue(String.class);
                mobileNumberET.setText(mobileNumber);

                String aatharNumber = dataSnapshot.child("aadharNumber").getValue(String.class);
                aatharNumberET.setText(aatharNumber);

                String cafNumber = dataSnapshot.child("cafNumber").getValue(String.class);
                cafET.setText(cafNumber);

                String setUpSerial = dataSnapshot.child("setUpBoxSerial").getValue(String.class);
                setupBoxSerialET.setText(setUpSerial);

                dialog.dismiss();

                materialProgressBar.stopProgressBar();
                Log.d(TAG, "onDataChange: progressbar Dismissed");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
     * onClick add button checks fields and if true changes are made in database
     */
    private void onClickAdd() {
        String area = areaTV.getText().toString();
        Log.d(TAG, "onClickAdd: area = " + area);

        String date = dateTV.getText().toString();
        final String connectionNumber = connectionNumberTV.getText().toString();

        String monthlyAmount = monthlyAmountET.getText().toString();
        Log.d(TAG, "onClickAdd: monthlyAmount = " + monthlyAmount);

        String name = nameET.getText().toString();
        Log.d(TAG, "onClickAdd: name = " + name);

        String mobileNumber = mobileNumberET.getText().toString();
        Log.d(TAG, "onClickAdd: mobileNumber = " + mobileNumber);

        String aadharNumber = aatharNumberET.getText().toString();
        Log.d(TAG, "onClickAdd: aatharNumber = " + aadharNumber);

        String cafNumber = cafET.getText().toString();
        Log.d(TAG, "onClickAdd: cafNumber = " + cafNumber);

        String setUpBoxSerial = setupBoxSerialET.getText().toString();
        Log.d(TAG, "onClickAdd: setUpBoxSerial = " + setUpBoxSerial);

        if (monthlyAmount.isEmpty() || monthlyAmount.length() < 3 || monthlyAmount.length() > 3) {
            displayErrorDialog("Enter Monthly Amount", "Monthly Amount Should Contain 3 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (name.isEmpty() || name.length() < 1 || name.length() > 20) {
            displayErrorDialog("Enter Name", "Name Should Contain 1-20 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (mobileNumber.isEmpty() || mobileNumber.length() < 10 || mobileNumber.length() > 10) {
            displayErrorDialog("Enter Mobile Number", "Mobile Number Should Contain 10 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (aadharNumber.isEmpty() || aadharNumber.length() < 12 || aadharNumber.length() > 12) {
            displayErrorDialog("Enter Aathar Number", "Aathar Number Should Contain 12 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (cafNumber.isEmpty() || cafNumber.length() < 7 || cafNumber.length() > 7) {
            displayErrorDialog("Enter CAF Number", "CAF Number Should Contain 7 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (setUpBoxSerial.isEmpty() || setUpBoxSerial.length() < 8 || setUpBoxSerial.length() > 8) {
            displayErrorDialog("Enter SetTop Box Serial Number", "SetTop Box Serial Should Contain 8 Characters").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (!checkFirebaseCharecters.checkFirebaseCharecters(connectionNumber)) {
            displayErrorDialog("Error", "Connection Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (!checkFirebaseCharecters.checkFirebaseCharecters(monthlyAmount)) {
            displayErrorDialog("Error", "Monthly Amount Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (!checkFirebaseCharecters.checkFirebaseCharecters(name)) {
            displayErrorDialog("Error", "Name Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (!checkFirebaseCharecters.checkFirebaseCharecters(mobileNumber)) {
            displayErrorDialog("Error", "Mobile Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (!checkFirebaseCharecters.checkFirebaseCharecters(aadharNumber)) {
            displayErrorDialog("Error", "Aathar Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (!checkFirebaseCharecters.checkFirebaseCharecters(cafNumber)) {
            displayErrorDialog("Error", "CAF Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else if (!checkFirebaseCharecters.checkFirebaseCharecters(setUpBoxSerial)) {
            displayErrorDialog("Error", "SetUp Box Number Should not contain . $ [ ] # / ").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        } else {
            materialProgressBar.startProgressBar(EditConnectionActivity.this);
            if (paidCB.isChecked()){
                nodes.getNodePaidList().child(area).child(connectionNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()){
                            materialProgressBar.stopProgressBar();
                            Toast.makeText(EditConnectionActivity.this,"Connection Paid",Toast.LENGTH_SHORT).show();
                        } else {
                            materialProgressBar.stopProgressBar();
                            Toast.makeText(EditConnectionActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBTN:
                onClickAdd();
                break;

            case R.id.cancelBtn:
                onClickAdd();
                break;
        }
    }
}
