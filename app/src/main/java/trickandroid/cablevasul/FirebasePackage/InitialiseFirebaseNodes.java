package trickandroid.cablevasul.FirebasePackage;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon Peter on 01-Nov-17.
 */

public class InitialiseFirebaseNodes {
    private getUserID getUserID = new getUserID();
    private DateSetter dateSetter = new DateSetter();
    private static final String TAG = "InitialiseFirebaseNodes";
    private FirebaseDatabase mainNode = FirebaseDatabase.getInstance();

    public DatabaseReference getNodeAreaList(){
        return mainNode.getReference().child(getUserID.getUserId()).child("AreaList");
    }

    public DatabaseReference getNodePaidList(){
        return mainNode.getReference().child(getUserID.getUserId()).child("PaidList");
    }

    public DatabaseReference getNodePendingList(){
        return mainNode.getReference().child(getUserID.getUserId()).child("PendingList");
    }

    public DatabaseReference getNodeAreaDetails(){
        return mainNode.getReference().child(getUserID.getUserId()).child("AreaDetails");
    }

    public DatabaseReference getNodeMonthDetails(){
        return mainNode.getReference().child(getUserID.getUserId()).child("MonthList");
    }

    public DatabaseReference getNodeVasulList(){
        return mainNode.getReference().child(getUserID.getUserId()).child("VasulList");
    }

    public DatabaseReference getNodeTotalConnections(){
        return mainNode.getReference().child(getUserID.getUserId()).child("TotalConnections");
    }

    public DatabaseReference getNodePendingConnections(){
        return mainNode.getReference().child(getUserID.getUserId()).child("PendingConnections");
    }

    public DatabaseReference getNodeTotalAmount(){
        return mainNode.getReference().child(getUserID.getUserId()).child("TotalAmount");
    }

    public DatabaseReference getNodeAmountCollected(){
        return mainNode.getReference().child(getUserID.getUserId()).child("AmountCollected");
    }

    public DatabaseReference getNodeCheckConnectionNumber(){
        return mainNode.getReference().child(getUserID.getUserId()).child("ConnectionNumberCheck");
    }

    public DatabaseReference getNodeConnectionListPerMonth(){
        return mainNode.getReference().child(getUserID.getUserId()).child("ConnectionListPerMonth");
    }

    public DatabaseReference getNodeConnectionList(){
        return mainNode.getReference().child(getUserID.getUserId()).child("ConnectionList");
    }

    public DatabaseReference getNodeDailyList(){
        return mainNode.getReference().child(getUserID.getUserId()).child("DailyList");
    }

    public static class getUserID{
        public String getUserId() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                Log.d(TAG, "getUserId: " + userId);
                return userId;
            }
            return null;
        }
    }
}
