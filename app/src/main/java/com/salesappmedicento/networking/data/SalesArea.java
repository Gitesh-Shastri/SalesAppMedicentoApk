package com.salesappmedicento.networking.data;

import java.io.Serializable;

public class SalesArea implements Serializable {
    private String mAreaName;
    private String mCity;
    private String mState;
    private String mPincode;
    private String mId;

    public SalesArea(String areaName, String city, String state, String pincode, String id) {
        mAreaName = areaName;
        mCity = city;
        mState = state;
        mPincode = pincode;
        mId = id;
    }

    public String getAreaName() {
        return mAreaName;
    }

    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public String getId() {
        return mId;
    }

    public String getPincode() {
        return mPincode;
    }

    @Override
    public String toString() {
        return mAreaName;
    }
}
