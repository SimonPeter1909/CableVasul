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
 * Created by Simon Peter on 06-Nov-17.
 */

public class FragmentConnectionList extends Fragment {

    private static final String TAG = "FragmentConnectionList";

    private RecyclerView connectionListRV;
    private FirebaseRecyclerAdapter<NewConnectionDetails, ConnectionListViewHolder> adapter;
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();
    private DateSetter dateSetter = new DateSetter();

    public static FragmentConnectionList newInstance(){
        return new FragmentConnectionList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_connection_list,container,false);

        initializeWidgets(mainView);

        setAdapter(mainView);

        return mainView;
    }

    /**
     * gets AreaName from the fragment bundle and returns as a String
     * @return
     */
    public String getAreaName(){
        Bundle bundle = getArguments();
        Log.d(TAG, "getAreaName: = " + bundle.getString("areaName"));
        return bundle.getString("areaName");
    }

    /**
     * sets the value obtained from the firebase database and displays in the card View
     * @param view
     */
    public void setAdapter(View view){
        FirebaseRecyclerOptions<NewConnectionDetails> options = new FirebaseRecyclerOptions.Builder<NewConnectionDetails>()
                .setQuery(nodes.getNodeConnectionListPerMonth().child(getAreaName()).child(dateSetter.monthAndYear()),NewConnectionDetails.class)
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

            }

            @Override
            public ConnectionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_connection_list,parent,false);

                return new ConnectionListViewHolder(view1);
            }
        };

        connectionListRV.setHasFixedSize(true);
        connectionListRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        connectionListRV.setAdapter(adapter);
    }

    public void onClickCallImg(final ConnectionListViewHolder holder){
        holder.callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String connectionNumber = holder.connectionNumberTV.getText().toString();

            }
        });
    }

    /**
     * sets the TextColor of the PaidTV
     * @param holder
     */
    public void setTextColorofPaidTV(ConnectionListViewHolder holder){
        if (holder.paidTV.getText().equals("Paid")){
            holder.paidTV.setTextColor(getResources().getColor(R.color.Green));
        } else {
            holder.paidTV.setTextColor(getResources().getColor(R.color.Red));
        }
    }

    /**
     * initializing widgets of the Fragment
     * @param view
     */
    public void initializeWidgets(View view){
        connectionListRV = view.findViewById(R.id.connectionListRV);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
