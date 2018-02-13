package trickandroid.cablevasul.ActivityDailyList.Fragments;

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
import trickandroid.cablevasul.ActivityCustomerList.Functions.OnClickCallImage;
import trickandroid.cablevasul.ActivityCustomerList.Functions.OnHolderClick;
import trickandroid.cablevasul.ActivityCustomerList.ViewHolder.ConnectionListViewHolder;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon on 2/12/2018.
 */

public class FragmentDailyPendingList extends Fragment {
    private static final String TAG = "FragmentDailyPendingLis";

    private RecyclerView pendingListRV;
    private FirebaseRecyclerAdapter<NewConnectionDetails, ConnectionListViewHolder> adapter;
    //firebase
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    //utils
    private DateSetter dateSetter = new DateSetter();

    //functions
    private OnClickCallImage onClickCallImage = new OnClickCallImage();
    private OnHolderClick onHolderClick = new OnHolderClick();

    public static FragmentDailyPendingList newInstance(){
        return new FragmentDailyPendingList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pending_list,container,false);

        initializeWidgets(view);
        setAdapter(view);

        return view;

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
     * gets AreaName from the fragment bundle and returns as a String
     * @return
     */
    private String getDate(){
        Bundle bundle = getArguments();
        Log.d(TAG, "getDate: = " + bundle.getString("date"));
        return bundle.getString("date");
    }

    /**
     * sets the value obtained from the firebase database and displays in the card View
     * @param view
     */
    public void setAdapter(View view){
        FirebaseRecyclerOptions<NewConnectionDetails> options = new FirebaseRecyclerOptions.Builder<NewConnectionDetails>()
                .setQuery(nodes.getNodeDailyList().child(getDate().replace("/",",")).child("Connection List").child(getAreaName()).child("pending list"),NewConnectionDetails.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<NewConnectionDetails, ConnectionListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ConnectionListViewHolder holder, int position, NewConnectionDetails model) {
                holder.connectionNumberTV.setText(model.getConnectionNumber());
                holder.nameTV.setText(model.getName());
                holder.amountTV.setText(model.getMonthlyAmount());
                holder.connectionDateTV.setText(model.getDate());
                holder.paidTV.setText(model.getPaid());
                onClickCallImage.onClickCallImg(holder,getContext(),getAreaName());
                onHolderClick.onHolderClick(holder,model,getContext(),getAreaName());
                onHolderClick.setTextColorofPaidTV(holder,getContext());
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

    /**
     * initializing widgets of the Fragment
     * @param view
     */
    public void initializeWidgets(View view){
        pendingListRV = view.findViewById(R.id.pendingListRV);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
