package com.salesappmedicento.helperData;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.salesappmedicento.fragments.Monthly;
import com.salesappmedicento.fragments.Today;
import com.salesappmedicento.fragments.Weekly;

public class StatsFragmentAdapter extends FragmentStatePagerAdapter {
    public StatsFragmentAdapter(FragmentManager fm){
        super(fm);
    }
    @Override    public Fragment getItem(int position) {
        switch (position){
            case 0: return new Today();
            case 1: return new Weekly();
            case 2: return new Monthly();
        }
        return null;
    }
    @Override
    public int getCount() {
        return 3;
    }
    @Override    public CharSequence getPageTitle(int position) {        switch (position){
        case 0: return "Today";
        case 1: return "Weekly";
        case 2: return "Monthly";
        default: return null;
    }
    }
}
