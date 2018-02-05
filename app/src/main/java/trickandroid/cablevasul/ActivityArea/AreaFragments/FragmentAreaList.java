package trickandroid.cablevasul.ActivityArea.AreaFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import trickandroid.cablevasul.ActivityArea.Details.AreaDetails;
import trickandroid.cablevasul.ActivityArea.ViewHolder.AreaViewHolder;
import trickandroid.cablevasul.ActivityCustomerList.CustomerListActivity;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;

/**
 * Created by Simon on 1/21/2018.
 */

public class FragmentAreaList extends Fragment {

    private RecyclerView areaRV;
    private FirebaseRecyclerAdapter<AreaDetails, AreaViewHolder> areaAdapter;
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    public static FragmentAreaList newInstance(){
        return new FragmentAreaList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_area_list,container,false);
        areaRV = mainView.findViewById(R.id.areaRV);

        setAreaAdapter(mainView);

        return mainView;
    }


    /**
     * Adapter to display areaList
     * onClick actions for list Items
     */
    public void setAreaAdapter(View view) {
        FirebaseRecyclerOptions<AreaDetails> areaDetail = new FirebaseRecyclerOptions.Builder<AreaDetails>()
                .setQuery(nodes.getNodeAreaDetails(), AreaDetails.class)
                .build();

        areaAdapter = new FirebaseRecyclerAdapter<AreaDetails, AreaViewHolder>(areaDetail) {
            @Override
            protected void onBindViewHolder(final AreaViewHolder holder, int position, AreaDetails model) {
                holder.areaTV.setText(model.getAreaName());
                holder.totalConnectionTV.setText(String.valueOf(model.getTotalConnections()));
                holder.pendingTV.setText(String.valueOf(model.getPendingConnections()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String areaName = holder.areaTV.getText().toString();
                        onClickAreaHolder(areaName);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

            }

            @Override
            public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_area_list, parent, false);
                return new AreaViewHolder(view);
            }
        };

        areaRV.setHasFixedSize(true);
        areaRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        areaRV.setAdapter(areaAdapter);
    }

    /**
     * On clicking the area list opens the CustomerListActivity
     * @param areaName
     */
    public void onClickAreaHolder(String areaName){
        Intent mainActivity = new Intent(getActivity(), CustomerListActivity.class);
        mainActivity.putExtra("areaName", areaName);
        startActivity(mainActivity);
    }

    @Override
    public void onStart() {
        super.onStart();
        areaAdapter.startListening();
    }
}
