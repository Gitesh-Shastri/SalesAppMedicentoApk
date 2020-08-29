package com.salesappmedicento.actvity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.salesappmedicento.R;
import com.salesappmedicento.helperData.PharmacyCount;

import java.util.ArrayList;

public class ParticularTabDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_tab_details);

        ArrayList<String> pharmacyName = new ArrayList<>();
        pharmacyName.add("Vamsi Medicals");
        pharmacyName.add("A.P Pharma");
        pharmacyName.add("Medishop");
        pharmacyName.add("Kiran Medicals Chemist & Drugist");

        ArrayList<String> count = new ArrayList<>();
        count.add("4");
        count.add("2");
        count.add("1");
        count.add("3");

        ArrayList<String> netAmount = new ArrayList<>();
        netAmount.add("3200");
        netAmount.add("1000");
        netAmount.add("500");
        netAmount.add("2100");


        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        PharmacyCount pharmacyCount = new PharmacyCount(pharmacyName, count, netAmount, this);
        recyclerView.setAdapter(pharmacyCount);
    }
}
