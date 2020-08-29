package com.salesappmedicento.helperData;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salesappmedicento.R;
import com.salesappmedicento.networking.Api;
import com.salesappmedicento.networking.data.GetMedicineResponse;
import com.salesappmedicento.networking.data.Medicine;
import com.salesappmedicento.networking.data.MedicineAuto;
import com.salesappmedicento.networking.data.MedicineResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AutoCompleteAdapter extends ArrayAdapter<MedicineAuto> {

    private List<MedicineAuto> medicineAutos, tempNames;
    private SharedPreferences mSharedPreferences;
    private boolean isLoading = false;

    public AutoCompleteAdapter(Context context, ArrayList<MedicineAuto> objects) {
        super(context, R.layout.medicine_row, objects);
        this.medicineAutos = new ArrayList<>(objects);
        this.tempNames = new ArrayList<>(objects);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    @Override
    public MedicineAuto getItem(int position) {
        return medicineAutos.get(position);
    }

    @Override
    public int getCount() {
        return medicineAutos.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MedicineAuto medicineAuto = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.medicine_row,parent,false
            );
        }

        TextView name, company, mrp, medicine_discount, packing;

        name = convertView.findViewById(R.id.medicine_name_auto);
        company = convertView.findViewById(R.id.company_name_auto);
        mrp = convertView.findViewById(R.id.medicine_mrp_auto);
        medicine_discount = convertView.findViewById(R.id.medicine_discount);
        packing  = convertView.findViewById(R.id.packing);


        if(medicineAuto != null) {
            medicine_discount.setText(medicineAuto.getDiscount());
            name.setText(medicineAuto.getName());
            company.setText("Company : " + medicineAuto.getCompany());
            mrp.setText("\u20B9"+medicineAuto.getMrp()+"");
            if(!medicineAuto.getPacking().isEmpty()) {
                packing.setText(medicineAuto.getPacking());
            } else {
                packing.setText(" - ");
            }
        }

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return medicineFilter;
    }

    Filter medicineFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            MedicineAuto medicineAuto = (MedicineAuto) resultValue;
            return medicineAuto.getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            final List<MedicineAuto> medicineautos1 = new ArrayList<>();

            if(constraint == null || constraint.length() == 0 ) {
//                medicineautos.addAll(tempNames);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                if (!(tempNames != null && tempNames.size() > 0)) {
                    Log.d("Data", "performFiltering: not in temp");
                    final String json = mSharedPreferences.getString("medicines_saved", null);
                    Type type = new TypeToken<ArrayList<Medicine>>() {
                    }.getType();
                    tempNames = new Gson().fromJson(json, type);

                    if (tempNames == null) {
                        Log.d("Data", "performFiltering: not in temp cache");
                        if (!isLoading && filterPattern.length() > 0) {

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(Api.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            Api api = retrofit.create(Api.class);

                            Call<GetMedicineResponse> getMedicineResponseCall = api.getMedicineResponseCall(filterPattern, "0");

                            getMedicineResponseCall.enqueue(new Callback<GetMedicineResponse>() {
                                @Override
                                public void onResponse(Call<GetMedicineResponse> call, Response<GetMedicineResponse> response) {
                                    try {
                                        GetMedicineResponse getMedicineResponse = response.body();

                                        for (MedicineResponse medicineResponse : getMedicineResponse.getMedicineResponses()) {
                                            medicineautos1.add(new MedicineAuto(medicineResponse.getItem_name(), medicineResponse.getManfc_name(), medicineResponse.getOffer_qty(), medicineResponse.getQty() + "", medicineResponse.getPacking(), medicineResponse.getMrp()));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    medicineAutos.clear();
                                    medicineAutos.addAll(medicineautos1);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<GetMedicineResponse> call, Throwable t) {
                                    medicineAutos.clear();
                                    medicineAutos.addAll(medicineautos1);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    } else {
                        for (MedicineAuto medicineAuto : tempNames) {
                            if (medicineAuto.getName().toLowerCase().startsWith(filterPattern)) {
                                medicineautos1.add(medicineAuto);
                            }
                        }
                    }
                } else {
                    for (MedicineAuto medicineAuto : tempNames) {
                        if (medicineAuto.getName().toLowerCase().startsWith(filterPattern)) {
                            medicineautos1.add(medicineAuto);
                        }
                    }
                }
            }

            results.values = medicineautos1;
            results.count = medicineautos1.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if(results.values != null) {
                Log.d("gitesh_rsult", results.count+"");
                medicineAutos.clear();
                medicineAutos.addAll((ArrayList<MedicineAuto>)results.values);
            }
            notifyDataSetChanged();
        }
    };
}



