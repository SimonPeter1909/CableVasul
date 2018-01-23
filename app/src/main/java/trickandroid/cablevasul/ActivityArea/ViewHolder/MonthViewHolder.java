package trickandroid.cablevasul.ActivityArea.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import trickandroid.cablevasul.R;

/**
 * Created by Simon on 1/21/2018.
 */

public class MonthViewHolder extends RecyclerView.ViewHolder {

    public TextView monthTV, totalConnectionTV, pendingTV, totalAmountTV, amountCollectedTV;

    public MonthViewHolder(View itemView) {
        super(itemView);
        monthTV = itemView.findViewById(R.id.monthTextView);
        totalConnectionTV = itemView.findViewById(R.id.totalConnectionTV);
        pendingTV = itemView.findViewById(R.id.pendingTV);
        totalAmountTV = itemView.findViewById(R.id.totalAmountTV);
        amountCollectedTV = itemView.findViewById(R.id.amountCollectedTV);
    }
}
