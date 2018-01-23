package trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import trickandroid.cablevasul.R;

/**
 * Created by Simon Peter on 03-Nov-17.
 */

public class FragmentPendingList extends Fragment {
    private static final String TAG = "FragmentPendingList";

    public static FragmentPendingList newInstance(){
        return new FragmentPendingList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pending_list,container,false);


        return view;

    }
}
