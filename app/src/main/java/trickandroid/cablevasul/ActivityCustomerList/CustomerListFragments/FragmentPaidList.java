package trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import trickandroid.cablevasul.ActivityCustomerList.CustomerListActivity;
import trickandroid.cablevasul.R;

/**
 * Created by Simon Peter on 03-Nov-17.
 */

public class FragmentPaidList extends Fragment {
    private static final String TAG = "FragmentPaidList";

    private CustomerListActivity customerListActivity = new CustomerListActivity();

    public static FragmentPaidList newInstance(){
        return new FragmentPaidList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_paid_list,container,false);

        return view;

    }
}
