package com.salesappmedicento.fragments;


import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.salesappmedicento.R;
import com.salesappmedicento.helperData.MyRecyclerViewAdapter;
import com.salesappmedicento.helperData.OrderedMedicineAdapter;
import com.salesappmedicento.networking.data.SalesArea;
import com.salesappmedicento.networking.data.SalesPharmacy;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceOrderAreaFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    ArrayList<SalesArea> mSalesAreaDetails;
    ArrayList<SalesPharmacy> mSalesPharmacyDetails, tempSalesPharmacies;

    AutoCompleteTextView areaAuto;
    Spinner area_spinner;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter pharmacyAreaArrayAdapter;

    SearchView searchView;

    OnPlaceOrderChangeListener placeOrderChangeListener;

    SalesPharmacy selectedPharmacy;

    SalesArea selectedArea;

    Context context;

    public void addAreaAndPharma(ArrayList<SalesArea> areas, ArrayList<SalesPharmacy> pharmacies, Context context) {
        this.mSalesAreaDetails = areas;
        this.mSalesPharmacyDetails = pharmacies;
        this.context = context;

        ArrayAdapter<SalesArea> areaArrayAdapter = new ArrayAdapter<SalesArea>(
                this.context, android.R.layout.simple_spinner_item, mSalesAreaDetails);

        selectedArea = mSalesAreaDetails.get(0);

        areaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tempSalesPharmacies = new ArrayList<>();
        tempSalesPharmacies.addAll(mSalesPharmacyDetails);
        area_spinner.setAdapter(areaArrayAdapter);
        areaAuto.setAdapter(areaArrayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        pharmacyAreaArrayAdapter = new MyRecyclerViewAdapter(this.context, mSalesPharmacyDetails);
        recyclerView.setAdapter(pharmacyAreaArrayAdapter);
        pharmacyAreaArrayAdapter.setClickListener(this);
        if(tempSalesPharmacies.size() > 0 ) {
            selectedPharmacy = tempSalesPharmacies.get(0);
        }
    }

    public PlaceOrderAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_order_area, container, false);

        area_spinner = view.findViewById(R.id.areaSpinner);

        searchView = view.findViewById(R.id.search);
        areaAuto = view.findViewById(R.id.areaAuto);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tempSalesPharmacies.clear();
                for (SalesPharmacy salesPharmacy : mSalesPharmacyDetails) {
                    if (salesPharmacy.getPharmacyName().toLowerCase().startsWith(newText)) {
                        tempSalesPharmacies.add(salesPharmacy);
                    }
                }
                pharmacyAreaArrayAdapter = new MyRecyclerViewAdapter(getContext(), tempSalesPharmacies);
                recyclerView.setAdapter(pharmacyAreaArrayAdapter);
                pharmacyAreaArrayAdapter.setClickListener(PlaceOrderAreaFragment.this);
                return true;
            }
        });

//        areaAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                tempSalesPharmacies = new ArrayList<>();
//                selectedArea = mSalesAreaDetails.get(i);
//                Log.d("id", selectedArea.getId());
//                for (SalesPharmacy salesPharmacy : mSalesPharmacyDetails) {
//                    if (selectedArea.getId().equals(salesPharmacy.getAreaId())) {
//                        tempSalesPharmacies.add(salesPharmacy);
//                    }
//                }
//                pharmacyAreaArrayAdapter = new MyRecyclerViewAdapter(getContext(), tempSalesPharmacies);
//                recyclerView.setAdapter(pharmacyAreaArrayAdapter);
//                pharmacyAreaArrayAdapter.setClickListener(PlaceOrderAreaFragment.this);
//            }
//        });
//
//        area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                tempSalesPharmacies = new ArrayList<>();
//                selectedArea = mSalesAreaDetails.get(i);
//                for (SalesPharmacy salesPharmacy : mSalesPharmacyDetails) {
//                    if (mSalesAreaDetails.get(i).getId().equals(salesPharmacy.getAreaId())) {
//                        tempSalesPharmacies.add(salesPharmacy);
//                    }
//                }
//                pharmacyAreaArrayAdapter = new MyRecyclerViewAdapter(getContext(), tempSalesPharmacies);
//                recyclerView.setAdapter(pharmacyAreaArrayAdapter);
//                pharmacyAreaArrayAdapter.setClickListener(PlaceOrderAreaFragment.this);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });

        recyclerView = view.findViewById(R.id.pharmacy_rv);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        setPharmacy(tempSalesPharmacies.get(position));
    }

    public void setPharmacy(SalesPharmacy selectedPharmacy) {
        placeOrderChangeListener.onPharmaSelected(selectedPharmacy);
    }

    public interface OnPlaceOrderChangeListener {
        void onPharmaSelected(SalesPharmacy salesPharmacy);
    }

    public void setAreaPharmaSelectListener(OnPlaceOrderChangeListener listener) {
        placeOrderChangeListener = listener;
    }

}
