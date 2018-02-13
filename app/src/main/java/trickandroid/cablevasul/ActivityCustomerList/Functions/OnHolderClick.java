package trickandroid.cablevasul.ActivityCustomerList.Functions;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import trickandroid.cablevasul.ActivityCustomerList.Details.NewConnectionDetails;
import trickandroid.cablevasul.ActivityCustomerList.Activities.DetailsActivity;
import trickandroid.cablevasul.ActivityCustomerList.ViewHolder.ConnectionListViewHolder;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon on 2/7/2018.
 */

public class OnHolderClick {
    private static final String TAG = "OnHolderClick";
    
    //utils
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private DateSetter dateSetter = new DateSetter();

    private SweetAlertDialog payDialog;
    private SweetAlertDialog loadingDialog;
    

    /**
     * since don't want to mess onBindViewHolder this method is called
     *
     * @param holder
     * @param model
     */
    public void onHolderClick(ConnectionListViewHolder holder, final NewConnectionDetails model, final Context context, final String areaName) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getPaid().equals("Paid")){
                    openDetailsActivity(model, context);
                } else {
                    SweetAlertDialog firstDialog = new SweetAlertDialog(context)
                            .setTitleText(model.getConnectionNumber())
                            .setContentText(model.getName() + "\n" + model.getDate())
                            .setConfirmText("Pay")
                            .setCancelText("Details")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    payDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Are you Sure want to Pay?")
                                            .setContentText("Rs." + model.getMonthlyAmount() + " for " + model.getName())
                                            .setConfirmText("Yes")
                                            .setCancelText("No")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                    payDialog.dismissWithAnimation();
                                                    loadingDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                                                            .setTitleText("Paying")
                                                            .setContentText("Please Wait!!!");
                                                    loadingDialog.show();


                                                    setConnectionListPaid(model, areaName);
                                                    shiftNode(model, areaName);
                                                    updateValues(model, areaName);
                                                    //to stop progressBarDialog
                                                    nodes.getNodePendingConnections().child("PendingConnections").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            loadingDialog.dismissWithAnimation();
                                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
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
                                            })
                                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                }
                                            });
                                    payDialog.show();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    openDetailsActivity(model, context);
                                }
                            });


                    firstDialog.setCancelable(true);
                    firstDialog.show();
                }

            }
        });
    }

    private void openDetailsActivity(NewConnectionDetails model, Context context){
        Intent i = new Intent(context, DetailsActivity.class);
        i.putExtra("date", model.getDate());
        i.putExtra("areaName", model.getAreaName());
        i.putExtra("connectionNumber",model.getConnectionNumber());
        i.putExtra("monthlyAmount",model.getMonthlyAmount());
        i.putExtra("name",model.getName());
        i.putExtra("mobileNumber",model.getMobileNumber());
        i.putExtra("aatharNumber",model.getAadharNumber());
        i.putExtra("cafNumber",model.getCafNumber());
        i.putExtra("setUpBoxNumber",model.getSetUpBoxSerial());
        i.putExtra("paidDate",model.getPaidDate());
        context.startActivity(i);
    }

    /**
     * Update the values of the corresponding nodes while paying
     * @param model
     */
    public void updateValues(final NewConnectionDetails model,final String areaName){

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
        nodes.getNodeAreaDetails().child(areaName).child("pendingConnections").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                nodes.getNodeAreaDetails().child(areaName).child("pendingConnections").setValue(i-1);
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

        //dailyList areaName node
        nodes.getNodeDailyList().child(date).child(model.getAreaName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pendingConnections = dataSnapshot.child("pendingConnections").getValue(Integer.class);

                nodes.getNodeDailyList().child(date).child(model.getAreaName()).child("pendingConnections").setValue(pendingConnections-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * adds new node to the nodePaidList
     * removes the value in the nodePendingList
     * @param model
     */
    public void shiftNode(NewConnectionDetails model, String areaName){
        NewConnectionDetails newConnectionDetails = new NewConnectionDetails(model.getDate(),model.getName(),model.getAreaName(),model.getMonthlyAmount(),model.getConnectionNumber(),model.getMobileNumber(),model.getAadharNumber(),model.getCafNumber(),model.getSetUpBoxSerial(),dateSetter.ddmmyyyy(),"Paid",model.getMonthAndYear(),model.getIntDate());
        nodes.getNodePaidList().child(areaName).child(dateSetter.monthAndYear()).child(model.getConnectionNumber()).setValue(newConnectionDetails);
        nodes.getNodeDailyList().child(model.getDate().replace("/",",")).child("Connection List").child(areaName).child("paidList").child(model.getConnectionNumber()).setValue(newConnectionDetails);

        nodes.getNodePendingList().child(areaName).child(dateSetter.monthAndYear()).child(model.getConnectionNumber()).setValue(null);

        nodes.getNodeDailyList().child(model.getDate().replace("/",",")).child("Connection List").child(areaName).child("pending list").child(model.getConnectionNumber()).setValue(null);
    }

    /**
     * sets the value of paid node as Paid in connectionListPerMonth node
     * sets the value of paid date to date in connectionListPerMonth node
     * @param model
     */
    public void setConnectionListPaid(NewConnectionDetails model, String areaName) {
        nodes.getNodeConnectionListPerMonth()
                .child(areaName)
                .child(dateSetter.monthAndYear())
                .child(model.getConnectionNumber())
                .child("paid").setValue("Paid");
        nodes.getNodeConnectionListPerMonth()
                .child(areaName)
                .child(dateSetter.monthAndYear())
                .child(model.getConnectionNumber())
                .child("paidDate").setValue(dateSetter.ddmmyyyy());
    }


    /**
     * sets the TextColor of the PaidTV
     *
     * @param holder
     */
    public void setTextColorofPaidTV(ConnectionListViewHolder holder, Context context) {
        if (holder.paidTV.getText().equals("Paid")) {
            holder.paidTV.setTextColor(context.getResources().getColor(R.color.Green));
        } else {
            holder.paidTV.setTextColor(context.getResources().getColor(R.color.Red));
        }
    }


}
