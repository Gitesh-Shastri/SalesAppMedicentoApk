package com.developer.medicento.salesappmedicento.actvity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.developer.medicento.salesappmedicento.R;
import com.developer.medicento.salesappmedicento.helperData.OrderMedicineAdapter;
import com.developer.medicento.salesappmedicento.helperData.OrderedMedicine;
import com.developer.medicento.salesappmedicento.helperData.OrderedMedicineAdapter;

import java.util.ArrayList;

public class ParticularOrderDetails extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_order_details);

        ArrayList<OrderedMedicine> medicines = new ArrayList<>();
        medicines.add(new OrderedMedicine("EB MAX TAB",
                "MMC PHARMA",
                2,
                130,
                130,
                "1022"));
        medicines.add(new OrderedMedicine("EB MAX TAB",
                "FRANCO (INDIA II)",
                1,
                87,
                87,
                "1022"));


        recyclerView = findViewById(R.id.recycler_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OrderMedicineAdapter orderedMedicineAdapter = new OrderMedicineAdapter(this, medicines);
        recyclerView.setAdapter(orderedMedicineAdapter);
    }
}
