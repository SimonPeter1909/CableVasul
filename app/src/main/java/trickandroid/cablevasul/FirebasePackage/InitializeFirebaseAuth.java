package trickandroid.cablevasul.FirebasePackage;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Simon Peter on 03-Nov-17.
 */

public class InitializeFirebaseAuth {
    private static final String TAG = "InitializeFirebaseAuth";

    public FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuth mAuth(){
        return FirebaseAuth.getInstance();
    }

    public void initializeFBAuth() {
        mAuth();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    public void addAuthListener() {
        mAuth().addAuthStateListener(mAuthListener);
    }

    public void removeAuthListner() {
        if (mAuthListener != null) {
            mAuth().removeAuthStateListener(mAuthListener);
        }
    }

}
