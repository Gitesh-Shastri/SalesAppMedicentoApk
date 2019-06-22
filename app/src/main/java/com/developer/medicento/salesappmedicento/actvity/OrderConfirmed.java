package com.developer.medicento.salesappmedicento.actvity;

import android.content.Intent;
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

import com.developer.medicento.salesappmedicento.R;
import com.developer.medicento.salesappmedicento.helperData.OrderMedicineAdapter;
import com.developer.medicento.salesappmedicento.helperData.OrderedMedicine;
import com.developer.medicento.salesappmedicento.helperData.OrderedMedicineAdapter;
import com.developer.medicento.salesappmedicento.networking.data.SalesPharmacy;

import java.util.ArrayList;

public class OrderConfirmed extends AppCompatActivity {

    TextView mOrderIdTv, mSelectedPharmacyTv, mDeliveryDateTv, mTotalCostTv;
    SalesPharmacy mSelectedPharmacy;
    RecyclerView mRecyclerView;
    BottomSheetBehavior mBottomSheetBehavior;
    OrderMedicineAdapter mAdapter;
    Button mShareBtn,mNewOrder;
    ListView listview = null;

    float overallCost;

    String mOrderId, mDeliveryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);


        mOrderIdTv = findViewById(R.id.order_id_tv);
        mSelectedPharmacyTv = findViewById(R.id.selected_pharmacy_tv);
        mDeliveryDateTv = findViewById(R.id.delivery_date_tv);
        mTotalCostTv = findViewById(R.id.total_cost_tv);
        View bottomView = findViewById(R.id.bottom_sheet);


        mRecyclerView = findViewById(R.id.order_confirmed_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        ArrayList<OrderedMedicine> orderedMedicines = (ArrayList<OrderedMedicine>) getIntent().getSerializableExtra("orderDetails");

        mAdapter = new OrderMedicineAdapter(this, orderedMedicines);
        mRecyclerView.setAdapter(mAdapter);

        mOrderId = getIntent().getStringExtra("id");
        mDeliveryDate = getIntent().getStringExtra("date");


        for(OrderedMedicine orderedMedicine: orderedMedicines) {
            overallCost += orderedMedicine.getCost();
        }

        mOrderIdTv.setText(mOrderId);
        mDeliveryDateTv.setText(mDeliveryDate);
        mSelectedPharmacyTv.setText(getIntent().getStringExtra("pharmacy"));
        mTotalCostTv.setText(String.valueOf(overallCost));
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomView);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior.setPeekHeight(300);
        final String orderShareDetails = "*Medicento Sales Order Summary*" +
                "\n*Order id*: " + mOrderId +
                "\n*Total Cost*: " + "Rs. " + "*" + overallCost + "*" +
                "\n*Delivery schedule*: " + mDeliveryDate;

        mNewOrder = findViewById(R.id.neworder);
        mNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("order_placed", "order_placed");
                Intent intent = new Intent(OrderConfirmed.this, PlaceOrderNewActivity.class);
                startActivity(intent);
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

}
