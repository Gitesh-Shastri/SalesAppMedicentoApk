package com.developer.medicento.salesappmedicento.fragments;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.developer.medicento.salesappmedicento.R;
import com.developer.medicento.salesappmedicento.actvity.PlaceOrderNewActivity;
import com.developer.medicento.salesappmedicento.helperData.AutoCompleteAdapter;
import com.developer.medicento.salesappmedicento.helperData.Constants;
import com.developer.medicento.salesappmedicento.helperData.OrderedMedicineAdapter;
import com.developer.medicento.salesappmedicento.networking.SalesDataLoader;
import com.developer.medicento.salesappmedicento.networking.data.Medicine;
import com.developer.medicento.salesappmedicento.networking.data.MedicineAuto;
import com.developer.medicento.salesappmedicento.helperData.OrderedMedicine;
import com.developer.medicento.salesappmedicento.networking.data.SalesPharmacy;

import java.util.ArrayList;

public class PlaceOrderSelectMedicineFragment extends Fragment implements OrderedMedicineAdapter.OverallCostChangeListener,
        LoaderManager.LoaderCallbacks<Object> {

    SalesPharmacy salesPharmacy;

    OnPlaceOrderAreaChangeListener placeOrderChangeListener;

    TextView cartSub;
    TextView pharmaName;
    Animation mAnimation;

    CardView placingOrder;

    ArrayList<Medicine> medicines;

    Context context;

    String slot;

    RecyclerView mOrderedMedicinesListView;
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteAdapter medicineAdapter;
    ArrayList<MedicineAuto> medicineAuto;
    ArrayList<String> medicine1;

    public OrderedMedicineAdapter mOrderedMedicineAdapter;

    TextView mTotalTv;

    int count;

    String Sid;

    public void addMedicine(ArrayList<Medicine> medicines, Context context) {
        this.context = context;
        this.medicines = medicines;
        for (Medicine med : medicines) {
            medicineAuto.add(new MedicineAuto(med.getMedicentoName(), med.getCompanyName(), med.getPrice()));
            medicine1.add(med.getMedicentoName());
        }

        medicineAdapter = new AutoCompleteAdapter(this.context, medicineAuto);
        autoCompleteTextView.setAdapter(medicineAdapter);
        autoCompleteTextView.setEnabled(true);
    }

    public PlaceOrderSelectMedicineFragment() {
        // Required empty public constructor
    }

    public void addPharmacy(SalesPharmacy salesPharmacy, String id) {
        this.salesPharmacy = salesPharmacy;
        this.Sid = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_order_select_medicine, container, false);

        placingOrder = view.findViewById(R.id.placing_order);

        pharmaName = view.findViewById(R.id.pharmacy_edit_tv);
        mTotalTv = view.findViewById(R.id.total_cost);
        autoCompleteTextView = view.findViewById(R.id.medicine_edit_tv);
        mOrderedMedicinesListView = view.findViewById(R.id.ordered_medicines_list);

        mOrderedMedicineAdapter = new OrderedMedicineAdapter(new ArrayList<OrderedMedicine>());
        mOrderedMedicineAdapter.setContext(inflater.getContext());
        mOrderedMedicinesListView.setLayoutManager(new LinearLayoutManager(context));
        mOrderedMedicinesListView.setHasFixedSize(true);
        mOrderedMedicinesListView.setAdapter(mOrderedMedicineAdapter);
        mOrderedMedicineAdapter.setOverallCostChangeListener(this);

        cartSub = view.findViewById(R.id.tv2);

        medicines = new ArrayList<>();
        pharmaName.setText(getString(R.string.selected_pharmacy, salesPharmacy.getPharmacyName()));
        medicine1 = new ArrayList<>();
        medicineAuto = new ArrayList<>();

        cartSub.setText(getString(R.string.cart_subtotal_0_items, "0"));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                autoCompleteTextView.setText("");
                Medicine medicine = null;
                for (Medicine med : medicines) {
                    if (med.getMedicentoName().equals(medicineAdapter.getItem(position).getName())) {
                        medicine = med;
                        break;
                    }
                }
                mOrderedMedicineAdapter.add(new OrderedMedicine(medicine.getMedicentoName(),
                        medicine.getCompanyName(),
                        1,
                        medicine.getPrice(),
                        medicine.getCode(),
                        medicine.getPrice(),
                        medicine.getMstock(),
                        medicine.getPacking()));
                float cost = Float.parseFloat(mTotalTv.getText().toString().substring(1));
                float overall = cost + medicine.getPrice();
                mTotalTv.setText("₹" + overall);
                count += 1;
                cartSub.setText(getString(R.string.cart_subtotal_0_items, mOrderedMedicineAdapter.getItemCount() + ""));
                mOrderedMedicinesListView.smoothScrollToPosition(0);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = (int) viewHolder.itemView.getTag();
                mOrderedMedicineAdapter.remove(pos);
            }
        }).attachToRecyclerView(mOrderedMedicinesListView);
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(200);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);


        return view;
    }


    public void placeOrder(String slot) {
        this.slot = slot;
        getActivity().getLoaderManager().initLoader(Constants.PLACE_ORDER_LOADER_ID , null, this);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        Log.i("orderPlaced", mOrderedMedicineAdapter.getList().get(0).getCode());
        placingOrder.setVisibility(View.VISIBLE);
        return new SalesDataLoader(getContext(), Constants.PLACE_ORDER_URL, getString(R.string.place_order_action),  salesPharmacy.getId(), Sid, slot, mOrderedMedicineAdapter.getList());
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        placingOrder.setVisibility(View.GONE);
        placeOrderChangeListener.onOrderPlaced(mOrderedMedicineAdapter, (String[]) data, salesPharmacy);
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    public interface OnPlaceOrderAreaChangeListener {
        void onOrderPlaced(OrderedMedicineAdapter adapter, String[] output, SalesPharmacy salesPharmacy);
    }

    @Override
    public void onCostChanged(float newCost, int qty, String type) {
        mTotalTv.setText("₹" + newCost);
        cartSub.setText(getString(R.string.cart_subtotal_0_items, mOrderedMedicineAdapter.getItemCount() + ""));
    }
    public void setAreaPharmaSelectListener(OnPlaceOrderAreaChangeListener listener) {
        placeOrderChangeListener = listener;
    }


}
