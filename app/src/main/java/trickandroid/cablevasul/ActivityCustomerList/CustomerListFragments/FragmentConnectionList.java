package trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import trickandroid.cablevasul.ActivityCustomerList.Details.NewConnectionDetails;
import trickandroid.cablevasul.ActivityCustomerList.DetailsActivity;
import trickandroid.cablevasul.ActivityCustomerList.Functions.OnClickCallImage;
import trickandroid.cablevasul.ActivityCustomerList.ViewHolder.ConnectionListViewHolder;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon Peter on 06-Nov-17.
 */

public class FragmentConnectionList extends Fragment {

    private static final String TAG = "FragmentConnectionList";

    private RecyclerView connectionListRV;
    private FirebaseRecyclerAdapter<NewConnectionDetails, ConnectionListViewHolder> adapter;
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private DateSetter dateSetter = new DateSetter();
    private OnClickCallImage onClickCallImage = new OnClickCallImage();

    //Dialogs
    private SweetAlertDialog firstDialog, payDialog, loadingDialog;

    public static FragmentConnectionList newInstance() {
        return new FragmentConnectionList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_connection_list, container, false);

        initializeWidgets(mainView);

        setAdapter(mainView);

        return mainView;
    }

    /**
     * gets AreaName from the fragment bundle and returns as a String
     *
     * @return
     */
    public String getAreaName() {
        Bundle bundle = getArguments();
        Log.d(TAG, "getAreaName: = " + bundle.getString("areaName"));
        return bundle.getString("areaName");
    }

    /**
     * sets the value obtained from the firebase database and displays in the card View
     *
     * @param view
     */
    public void setAdapter(View view) {
        FirebaseRecyclerOptions<NewConnectionDetails> options = new FirebaseRecyclerOptions.Builder<NewConnectionDetails>()
                .setQuery(nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()), NewConnectionDetails.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<NewConnectionDetails, ConnectionListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ConnectionListViewHolder holder, int position, NewConnectionDetails model) {
                holder.connectionNumberTV.setText(model.getConnectionNumber());
                holder.nameTV.setText(model.getName());
                holder.amountTV.setText(model.getMonthlyAmount());
                holder.connectionDateTV.setText(model.getDate());
                holder.paidTV.setText(model.getPaid());
                setTextColorofPaidTV(holder);
                onClickCallImage.onClickCallImg(holder,getContext(),getAreaName());
                onHolderClick(holder, model);
            }

            @Override
            public ConnectionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_connection_list, parent, false);

                return new ConnectionListViewHolder(view1);
            }
        };

        connectionListRV.setHasFixedSize(true);
        connectionListRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        connectionListRV.setAdapter(adapter);
    }

    /**
     * since don't want to mess onBindViewHolder this method is called
     *
     * @param holder
     * @param model
     */
    public void onHolderClick(ConnectionListViewHolder holder, final NewConnectionDetails model) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getPaid().equals("Paid")){
                    openDetailsActivity(model);
                } else {
                    openFirstDialog(model);
                }

            }
        });
    }

    /**
     * opens FirstDialog when the item is Clicked
     *
     * @param model
     */
    public void openFirstDialog(final NewConnectionDetails model) {
        firstDialog = new SweetAlertDialog(getContext())
                .setTitleText(model.getConnectionNumber())
                .setContentText(model.getName() + "\n" + model.getDate())
                .setConfirmText("Pay")
                .setCancelText("Details")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        openPayDialog(model);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        openDetailsActivity(model);
                    }
                });


        firstDialog.setCancelable(true);
        firstDialog.show();
    }

    private void openDetailsActivity(NewConnectionDetails model){
        Intent i = new Intent(getContext(), DetailsActivity.class);
        i.putExtra("connectionNumber",model.getConnectionNumber());
        i.putExtra("monthlyAmount",model.getMonthlyAmount());
        i.putExtra("name",model.getName());
        i.putExtra("mobileNumber",model.getMobileNumber());
        i.putExtra("aatharNumber",model.getAadharNumber());
        i.putExtra("cafNumber",model.getCafNumber());
        i.putExtra("setUpBoxNumber",model.getSetUpBoxSerial());
        i.putExtra("paidDate",model.getPaidDate());
        startActivity(i);
    }

    /**
     * opens Pay Dialog
     *
     * @param model
     */
    public void openPayDialog(final NewConnectionDetails model) {
        payDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you Sure want to Pay?")
                .setContentText("Rs." + model.getMonthlyAmount() + " for " + model.getName())
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        payDialog.dismissWithAnimation();
                        openLoadingDialog(model);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
        payDialog.show();
    }

    /**
     * opens loading progress dialog
     *
     * @param model
     */
    public void openLoadingDialog(final NewConnectionDetails model) {
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Paying")
                .setContentText("Please Wait!!!");
        loadingDialog.show();
        paying(model);
    }

    /**
     * this method consists all the methods that are happening while the progress bar is loading
     * @param model
     */
    public void paying(final NewConnectionDetails model) {

        setConnectionListPaid(model);

        shiftNode(model);

        updateValues(model);

        //to stop progressBarDialog
        nodes.getNodePendingConnections().child("PendingConnections").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadingDialog.dismissWithAnimation();
                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setContentText("Rs." + model.getMonthlyAmount() + " paid Successfully")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Update the values of the corresponding nodes while paying
     * @param model
     */
    public void updateValues(final NewConnectionDetails model){

        //AmountCollected Node
        nodes.getNodeAmountCollected().child("AmountCollected").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int amount = dataSnapshot.getValue(Integer.class);
                nodes.getNodeAmountCollected().child("AmountCollected").setValue(amount + Integer.parseInt(model.getMonthlyAmount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //AreaDetails Node
        nodes.getNodeAreaDetails().child(getAreaName()).child("pendingConnections").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                nodes.getNodeAreaDetails().child(getAreaName()).child("pendingConnections").setValue(i-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //MonthList - amountCollected node
        nodes.getNodeMonthDetails().child(model.getMonthAndYear()).child("amountCollected").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                nodes.getNodeMonthDetails().child(model.getMonthAndYear()).child("amountCollected").setValue(i+Integer.parseInt(model.getMonthlyAmount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //MonthList - pendingConnections Node
        nodes.getNodeMonthDetails().child(model.getMonthAndYear()).child("pendingConnections").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                nodes.getNodeMonthDetails().child(model.getMonthAndYear()).child("pendingConnections").setValue(i-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //PendingConnections node
        nodes.getNodePendingConnections().child("PendingConnections").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                nodes.getNodePendingConnections().child("PendingConnections").setValue(i-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //dailyList node
        final String date = model.getDate().replace("/",",");

        //dailyList Notification node
        nodes.getNodeDailyList().child(date).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pendingConnections = dataSnapshot.child("pendingConnections").getValue(Integer.class);

                nodes.getNodeDailyList().child(date).child("notification").child("pendingConnections").setValue(pendingConnections-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //adds value to paidList in dailyList connection node
        NewConnectionDetails newConnectionDetails = new NewConnectionDetails(model.getDate(),model.getName(),model.getAreaName(),model.getMonthlyAmount(),model.getConnectionNumber(),model.getMobileNumber(),model.getAadharNumber(),model.getCafNumber(),model.getSetUpBoxSerial(),model.getPaidDate(),model.getPaid(),model.getMonthAndYear(),model.getIntDate());
        nodes.getNodeDailyList().child(date).child("Connection List").child("paidList").child(model.getConnectionNumber()).setValue(newConnectionDetails);

        //deletes node from pendingList
        nodes.getNodeDailyList().child(date).child("Connection List").child("pending list").child(model.getConnectionNumber()).setValue(null);
    }

    /**
     * adds new node to the nodePaidList
     * removes the value in the nodePendingList
     * @param model
     */
    public void shiftNode(NewConnectionDetails model){
        NewConnectionDetails newConnectionDetails = new NewConnectionDetails(model.getDate(),model.getName(),model.getAreaName(),model.getMonthlyAmount(),model.getConnectionNumber(),model.getMobileNumber(),model.getAadharNumber(),model.getCafNumber(),model.getSetUpBoxSerial(),dateSetter.ddmmyyyy(),"Paid",model.getMonthAndYear(),model.getIntDate());
        nodes.getNodePaidList().child(getAreaName()).child(dateSetter.monthAndYear()).child(model.getConnectionNumber()).setValue(newConnectionDetails);

        nodes.getNodePendingList()
                .child(getAreaName())
                .child(dateSetter.monthAndYear())
                .child(model.getConnectionNumber()).setValue(null);
    }

    /**
     * sets the value of paid node as Paid in connectionListPerMonth node
     * sets the value of paid date to date in connectionListPerMonth node
     * @param model
     */
    public void setConnectionListPaid(NewConnectionDetails model) {
        nodes.getNodeConnectionListPerMonth()
                .child(getAreaName())
                .child(dateSetter.monthAndYear())
                .child(model.getConnectionNumber())
                .child("paid").setValue("Paid");
        nodes.getNodeConnectionListPerMonth()
                .child(getAreaName())
                .child(dateSetter.monthAndYear())
                .child(model.getConnectionNumber())
                .child("paidDate").setValue(dateSetter.ddmmyyyy());
    }

    /**
     * onClick Call Button gets the value(mobileNumber) of the Corresponding User
     *
     * @param holder
     */
    public void onClickCallImg(final ConnectionListViewHolder holder) {
        holder.callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String connectionNumber = holder.connectionNumberTV.getText().toString();

                nodes.getNodeConnectionListPerMonth()
                        .child(getAreaName())
                        .child(dateSetter.monthAndYear())
                        .child(connectionNumber)
                        .child("mobileNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mobileNumber = dataSnapshot.getValue(String.class);
                        displayCallDialog(mobileNumber);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    /**
     * Display a Dialog that contains mobileNumber
     * When Call button is clicked it Opens the dailer to make a call
     *
     * @param mobileNumber
     */
    public void displayCallDialog(final String mobileNumber) {
        new MaterialDialog.Builder(getContext())
                .title("Call")
                .content("Are you sure want to Call " + mobileNumber)
                .iconRes(R.drawable.ic_phone)
                .cancelable(false)
                .positiveText("Call")
                .positiveColor(getResources().getColor(R.color.Blue))
                .negativeText("Cancel")
                .negativeColor(getResources().getColor(R.color.Red))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent call = new Intent(Intent.ACTION_DIAL);
                        call.setData(Uri.parse("tel:" + mobileNumber));
                        startActivity(call);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .cancelable(false)
                .show();

    }

    /**
     * sets the TextColor of the PaidTV
     *
     * @param holder
     */
    public void setTextColorofPaidTV(ConnectionListViewHolder holder) {
        if (holder.paidTV.getText().equals("Paid")) {
            holder.paidTV.setTextColor(getResources().getColor(R.color.Green));
        } else {
            holder.paidTV.setTextColor(getResources().getColor(R.color.Red));
        }
    }

    /**
     * initializing widgets of the Fragment
     *
     * @param view
     */
    public void initializeWidgets(View view) {
        connectionListRV = view.findViewById(R.id.connectionListRV);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
