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
import com.salesappmedicento.actvity.Recentorder;

import java.util.ArrayList;

public class PharmacyCount extends RecyclerView.Adapter<PharmacyCount.ViewHolder> {

    private Context context;
    private ArrayList<String> pharmacyName, count, netAmount;

    public PharmacyCount(ArrayList<String> pharmacyName, ArrayList<String> count, ArrayList<String> netAmount, Context context) {
        this.pharmacyName = pharmacyName;
        this.count = count;
        this.context = context;
        this.netAmount = netAmount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pharmacy, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.pharmacy_name.setText(this.pharmacyName.get(position));
        holder.pharmacy_count.setText("Total Orders : " + this.count.get(position));
        holder.net_amount.setText("Net Amount : ₹" + this.netAmount.get(position));
    }

    @Override
    public int getItemCount() {
        return this.pharmacyName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pharmacy_name, pharmacy_count, net_amount;

        public ViewHolder(View itemView) {
            super(itemView);

            pharmacy_count = itemView.findViewById(R.id.count);
            pharmacy_name = itemView.findViewById(R.id.pharmacy_name);
            net_amount = itemView.findViewById(R.id.net_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, Recentorder.class));
                }
            });
        }
    }
}
