package trickandroid.cablevasul.ActivityCustomerList.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import trickandroid.cablevasul.R;

/**
 * Created by Simon on 1/21/2018.
 */

public class ConnectionListViewHolder extends RecyclerView.ViewHolder {

    public TextView connectionNumberTV, nameTV, amountTV, connectionDateTV, paidTV;
    public ImageView callImg;

    public ConnectionListViewHolder(View itemView) {
        super(itemView);
        connectionNumberTV = itemView.findViewById(R.id.connectionNumberTV);
        nameTV = itemView.findViewById(R.id.nameTV);
        amountTV = itemView.findViewById(R.id.amountTV);
        connectionDateTV = itemView.findViewById(R.id.connectionDateTV);
        paidTV = itemView.findViewById(R.id.paidTV);
        callImg = itemView.findViewById(R.id.callImg);
    }
}
