package com.medicento.salesappmedicento;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.medicento.salesappmedicento.helperData.StatsFragmentAdapter;

public class StatsActivity extends AppCompatActivity {
    ViewPager stats;
    StatsFragmentAdapter statAdapter;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        stats = (ViewPager) findViewById(R.id.stats_pager);
        statAdapter = new StatsFragmentAdapter(getSupportFragmentManager());
        stats.setAdapter(statAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(stats);
    }
}
