package trickandroid.cablevasul.ActivityCustomerList.CustomerListFragments;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import trickandroid.cablevasul.ActivityCustomerList.Details.NewConnectionDetails;
import trickandroid.cablevasul.ActivityCustomerList.ViewHolder.ConnectionListViewHolder;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon Peter on 03-Nov-17.
 */

public class FragmentPendingList extends Fragment {
    private static final String TAG = "FragmentPendingList";

    private RecyclerView pendingListRV;
    private FirebaseRecyclerAdapter<NewConnectionDetails, ConnectionListViewHolder> adapter;
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private DateSetter dateSetter = new DateSetter();

    public static FragmentPendingList newInstance(){
        return new FragmentPendingList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_pending_list,container,false);

        initializeWidgets(mainView);

        setAdapter(mainView);

        return mainView;

    }

    public String getAreaName(){
        Bundle bundle = getArguments();
        Log.d(TAG, "getAreaName: = " + bundle.getString("areaName"));
        return bundle.getString("areaName");
    }

    public void setAdapter(View view){
        FirebaseRecyclerOptions<NewConnectionDetails> options = new FirebaseRecyclerOptions.Builder<NewConnectionDetails>()
                .setQuery(nodes.getNodePendingList().child(getAreaName()).child(dateSetter.monthAndYear()),NewConnectionDetails.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<NewConnectionDetails, ConnectionListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ConnectionListViewHolder holder, int position, NewConnectionDetails model) {
                holder.connectionNumberTV.setText(model.getConnectionNumber());
                holder.nameTV.setText(model.getName());
                holder.amountTV.setText(model.getMonthlyAmount());
                holder.connectionDateTV.setText(model.getDate());
                holder.paidTV.setText(model.getPaid());
            }

            @Override
            public ConnectionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_connection_list,parent,false);

                return new ConnectionListViewHolder(view1);
            }
        };

        pendingListRV.setHasFixedSize(true);
        pendingListRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        pendingListRV.setAdapter(adapter);
    }

    public void initializeWidgets(View view){
        pendingListRV = view.findViewById(R.id.pendingListRV);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
