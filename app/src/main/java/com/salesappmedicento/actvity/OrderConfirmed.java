package com.salesappmedicento.actvity;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.salesappmedicento.R;
import com.salesappmedicento.helperData.OrderMedicineAdapter;
import com.salesappmedicento.helperData.OrderedMedicine;
import com.salesappmedicento.helperData.OrderedMedicineAdapter;
import com.salesappmedicento.networking.data.SalesPerson;
import com.salesappmedicento.networking.data.SalesPharmacy;
import com.salesappmedicento.networking.util.MedicentoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class OrderConfirmed extends AppCompatActivity {

    TextView mOrderIdTv, mSelectedPharmacyTv, mDeliveryDateTv, mTotalCostTv;
    SalesPharmacy mSelectedPharmacy;
    RecyclerView mRecyclerView;
    BottomSheetBehavior mBottomSheetBehavior;
    OrderMedicineAdapter mAdapter;
    Button mShareBtn, mNewOrder;
    ListView listview = null;

    float overallCost;

    String mOrderId, mDeliveryDate;
    String orderShareDetails;
    private SalesPerson salesPerson;
    JSONObject activityObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);

        String cache = Paper.book().read("user");

        activityObject = new JSONObject();

        if (cache != null && !cache.isEmpty()) {
            salesPerson = new Gson().fromJson(cache, SalesPerson.class);
        }

        mOrderIdTv = findViewById(R.id.order_id_tv);
        mSelectedPharmacyTv = findViewById(R.id.selected_pharmacy_tv);
        mDeliveryDateTv = findViewById(R.id.delivery_date_tv);
        mTotalCostTv = findViewById(R.id.total_cost_tv);

        mRecyclerView = findViewById(R.id.order_confirmed_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        ArrayList<OrderedMedicine> orderedMedicines = (ArrayList<OrderedMedicine>) getIntent().getSerializableExtra("orderDetails");

        mAdapter = new OrderMedicineAdapter(this, orderedMedicines);
        mRecyclerView.setAdapter(mAdapter);

        mOrderId = getIntent().getStringExtra("id");
        mDeliveryDate = getIntent().getStringExtra("date") + " " + getIntent().getStringExtra("slots");


        for (OrderedMedicine orderedMedicine : orderedMedicines) {
            overallCost += orderedMedicine.getCost();
        }

        mOrderIdTv.setText(mOrderId);
        mDeliveryDateTv.setText(mDeliveryDate);
        mSelectedPharmacyTv.setText(getIntent().getStringExtra("pharmacy"));
        mTotalCostTv.setText(String.valueOf(overallCost));

        orderShareDetails = "*Medicento Sales Order Summary*" +
                "\n*Order Mode*: Sales App" +
                "\n*Pharmacy Name*: " + getIntent().getStringExtra("pharmacy") +
                "\n*City*: Bangalore" +
                "\n\n*Order ID*: " + mOrderId +
                "\n*Total Cost*: " + "Rs. " + "*" + overallCost + "*" +
                "\n*Delivery schedule*: " + mDeliveryDate + "\n" +
                "Medicine Name | Qty | Approx. Cost \n";

        for (OrderedMedicine orderedMedicine : orderedMedicines) {
            orderShareDetails += orderedMedicine.getMedicineName() +
                    " | " + orderedMedicine.getQty() +
                    " | Rs." + orderedMedicine.getCost() + "\n";
        }

        mNewOrder = findViewById(R.id.neworder);
        mNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mShareBtn = findViewById(R.id.share_order_btn);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_SUBJECT, "Medicento Order details");
                intent.putExtra(Intent.EXTRA_TEXT, orderShareDetails);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderConfirmed.this, PharmacySelectionActivity.class));
        finish();
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            activityObject.put("activity_name", "OrderConfirmed");
            activityObject.put("start_time", System.currentTimeMillis() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        String androidId = "";
        try {
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            activityObject.put("end_time", System.currentTimeMillis() + "");
            activityObject.put("android_id", androidId);
            if (salesPerson != null && salesPerson.getId() != null) {
                activityObject.put("sales_id", salesPerson.getId());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/record_activity/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MedicentoUtils.showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d("data", "getParams: " + activityObject.toString());
                try {
                    params.put("activity_name", activityObject.getString("activity_name"));
                    params.put("start_time", activityObject.getString("start_time"));
                    params.put("end_time", activityObject.getString("end_time"));
                    params.put("android_id", activityObject.getString("android_id"));
                    params.put("sales_id", activityObject.getString("sales_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
