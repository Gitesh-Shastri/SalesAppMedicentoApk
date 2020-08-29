package com.salesappmedicento.helperData;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salesappmedicento.R;

import java.util.ArrayList;

public class OrderMedicineAdapter extends RecyclerView.Adapter<OrderMedicineAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderedMedicine> medicines;

    public OrderMedicineAdapter(Context context, ArrayList<OrderedMedicine> medicines) {
        this.context = context;
        this.medicines = medicines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_ordered_medicine, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.medicine_name.setText(medicines.get(position).getmMedicineName());
        holder.company_name.setText(medicines.get(position).getMedicineCompany());
        holder.price.setText(" ₹ "+medicines.get(position).getCost());
        holder.qty.setText("Qty: "+medicines.get(position).getmQty());
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicine_name, company_name, qty, price;

        public ViewHolder(View itemView) {
            super(itemView);

            medicine_name = itemView.findViewById(R.id.medicine_name);
            company_name = itemView.findViewById(R.id.medicine_company);
            qty = itemView.findViewById(R.id.medicine_quantity);
            price = itemView.findViewById(R.id.medicine_cost);
        }
    }
}
