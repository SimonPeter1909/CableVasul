package trickandroid.cablevasul.ActivityCustomerList.Functions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import trickandroid.cablevasul.ActivityCustomerList.ViewHolder.ConnectionListViewHolder;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon on 2/5/2018.
 */

public class OnClickCallImage {

    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private DateSetter dateSetter = new DateSetter();

    /**
     * onClick Call Button gets the value(mobileNumber) of the Corresponding User
     *
     * @param holder
     */
    public void onClickCallImg(final ConnectionListViewHolder holder, final Context context,final String areaName) {
        holder.callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String connectionNumber = holder.connectionNumberTV.getText().toString();

                nodes.getNodeConnectionListPerMonth()
                        .child(areaName)
                        .child(dateSetter.monthAndYear())
                        .child(connectionNumber)
                        .child("mobileNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String mobileNumber = dataSnapshot.getValue(String.class);

                        new MaterialDialog.Builder(context)
                                .title("Call")
                                .content("Are you sure want to Call " + mobileNumber)
                                .iconRes(R.drawable.ic_phone)
                                .cancelable(false)
                                .positiveText("Call")
                                .positiveColor(context.getResources().getColor(R.color.Blue))
                                .negativeText("Cancel")
                                .negativeColor(context.getResources().getColor(R.color.Red))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent call = new Intent(Intent.ACTION_DIAL);
                                        call.setData(Uri.parse("tel:" + mobileNumber));
                                        context.startActivity(call);
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
