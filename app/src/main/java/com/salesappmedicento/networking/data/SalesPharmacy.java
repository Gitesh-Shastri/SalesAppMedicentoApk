package com.salesappmedicento.networking.data;

import java.io.Serializable;

public class SalesPharmacy implements Serializable {
    private String mPharmacyName;
    private String mPharmacyAddress;
    private String mId;
    private String mAreaId;
    private int creditsAvailable;
    private String mArea;

    public String getmArea() {
        return mArea;
    }

    public void setmArea(String mArea) {
        this.mArea = mArea;
    }

    public SalesPharmacy (String phamaName, String address, String id, String areaId) {
        mPharmacyName = phamaName;
        mPharmacyAddress = address;
        mId = id;
        mAreaId = areaId;
    }

    public int getCreditsAvailable() {
        return creditsAvailable;
    }

    public void setCreditsAvailable(int creditsAvailable) {
        this.creditsAvailable = creditsAvailable;
    }

    public String getPharmacyName() {
        return mPharmacyName;
    }

    public String getPharmacyAddress() {
        return mPharmacyAddress;
    }

    public String getId() {
        return mId;
    }

    @Override
    public String toString() {
        return mPharmacyName;
    }

    public String getAreaId() {
        return mAreaId;
    }
}
