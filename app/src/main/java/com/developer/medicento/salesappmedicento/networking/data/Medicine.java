package com.developer.medicento.salesappmedicento.networking.data;

import java.io.Serializable;

public class Medicine implements Serializable{
    private String mMedicentoName;
    private String mCompanyName;
    private int mPrice;
    private String mId;
    private String code;
    private int mstock;
    private String Packing;

    public Medicine (String medicentoName, String companyName, int price, String id, int stock, String code) {
        mMedicentoName = medicentoName;
        mCompanyName = companyName;
        mPrice = price;
        mId = id;
        this.code = code;
        mstock = stock;
    }

    public String getPacking() {
        return Packing;
    }

    public void setPacking(String packing) {
        Packing = packing;
    }

    public String getCode() {
        return code;
    }

    public String getMedicentoName() {
        return mMedicentoName;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public int getPrice() {
        return mPrice;
    }

    public String getId() {
        return mId;
    }

    public int getMstock() {
        return mstock;
    }
}
