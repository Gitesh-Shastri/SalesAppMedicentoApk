package com.salesappmedicento.helperData;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salesappmedicento.R;
import com.salesappmedicento.actvity.ParticularOrderDetails;
import com.salesappmedicento.networking.data.RecentOrder;

import java.util.ArrayList;

public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RecentOrder> recentOrders;

    public RecentOrderAdapter(Context context, ArrayList<RecentOrder> recentOrders) {
        this.context = context;
        this.recentOrders = recentOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recent_order, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.id.setText("Order Id : "+recentOrders.get(position).getOrderId());
        holder.amount.setText("Total Amount : ₹"+recentOrders.get(position).getTotal());
        holder.status.setText("Status : "+recentOrders.get(position).getStatus());
        holder.date.setText("Date : "+recentOrders.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return this.recentOrders.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id, amount, status, date;

        public ViewHolder(View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.order_id);
            amount = itemView.findViewById(R.id.order_amount);
            status = itemView.findViewById(R.id.order_status);
            date = itemView.findViewById(R.id.order_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ParticularOrderDetails.class));
                }
            });
        }
    }
}
