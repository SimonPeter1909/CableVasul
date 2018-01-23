package trickandroid.cablevasul.ActivityArea.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import trickandroid.cablevasul.R;

/**
 * Created by Simon on 1/21/2018.
 */

public class AreaViewHolder extends RecyclerView.ViewHolder {

    public TextView areaTV, totalConnectionTV, pendingTV;

    public AreaViewHolder(View itemView) {
        super(itemView);
        areaTV = itemView.findViewById(R.id.areaTextView);
        totalConnectionTV = itemView.findViewById(R.id.totalConnectionTV);
        pendingTV = itemView.findViewById(R.id.pendingTV);
    }
}
