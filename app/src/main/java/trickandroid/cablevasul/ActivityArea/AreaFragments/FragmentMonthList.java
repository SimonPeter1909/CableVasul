package trickandroid.cablevasul.ActivityArea.AreaFragments;

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

import trickandroid.cablevasul.ActivityArea.Details.MonthDetails;
import trickandroid.cablevasul.ActivityArea.ViewHolder.MonthViewHolder;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;

/**
 * Created by Simon on 1/21/2018.
 */

public class FragmentMonthList extends Fragment {

    private RecyclerView monthRV;
    private FirebaseRecyclerAdapter<MonthDetails, MonthViewHolder> monthAdapter;
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    public static FragmentMonthList newInstance(){
        return new FragmentMonthList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_month_list,container,false);
        monthRV = mainView.findViewById(R.id.monthRV);

        setMonthAdapter(mainView);

        return mainView;
    }

    /**
     * Adapter to display monthList
     * onClick actions for list Items
     */
    public void setMonthAdapter(View view) {
        FirebaseRecyclerOptions<MonthDetails> monthDetail = new FirebaseRecyclerOptions.Builder<MonthDetails>()
                .setQuery(nodes.getNodeMonthDetails(), MonthDetails.class)
                .build();

        monthAdapter = new FirebaseRecyclerAdapter<MonthDetails, MonthViewHolder>(monthDetail) {
            @Override
            protected void onBindViewHolder(MonthViewHolder holder, int position, MonthDetails model) {
                holder.monthTV.setText(model.getMonth());
                holder.totalConnectionTV.setText(String.valueOf(model.getTotalConnections()));
                holder.pendingTV.setText(String.valueOf(model.getPendingConnections()));
                holder.totalAmountTV.setText(String.valueOf(model.getTotalAmount()));
                holder.amountCollectedTV.setText(String.valueOf(model.getAmountCollected()));
            }

            @Override
            public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_month_list, parent, false);

                return new MonthViewHolder(view);
            }
        };

        monthRV.setHasFixedSize(true);
        monthRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        monthRV.setAdapter(monthAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        monthAdapter.startListening();
    }
}
