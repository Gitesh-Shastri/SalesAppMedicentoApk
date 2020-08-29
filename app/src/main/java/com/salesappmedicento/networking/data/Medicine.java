package com.salesappmedicento.networking.data;

import java.io.Serializable;

public class Medicine implements Serializable{
    private String mMedicentoName;
    private String mCompanyName;
    private float mPrice;
    private String mId;
    private String code;
    private int mstock;
    private String Packing;
    private float mrp;
    private String scheme;
    private String discount, offer_qty;


    public String getmMedicentoName() {
        return mMedicentoName;
    }

    public void setmMedicentoName(String mMedicentoName) {
        this.mMedicentoName = mMedicentoName;
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public float getmPrice() {
        return mPrice;
    }

    public void setmPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMstock(int mstock) {
        this.mstock = mstock;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOffer_qty() {
        return offer_qty;
    }

    public void setOffer_qty(String offer_qty) {
        this.offer_qty = offer_qty;
    }

    public Medicine(String mMedicentoName, String mCompanyName, float mPrice, String mId, String code, int mstock, String packing, float mrp, String scheme, String discount, String offer_qty) {
        this.mMedicentoName = mMedicentoName;
        this.mCompanyName = mCompanyName;
        this.mPrice = mPrice;
        this.mId = mId;
        this.code = code;
        this.mstock = mstock;
        Packing = packing;
        this.mrp = mrp;
        this.scheme = scheme;
        this.discount = discount;
        this.offer_qty = offer_qty;
    }

    public Medicine (String medicentoName, String companyName, float price, String id, int stock, String code) {
        mMedicentoName = medicentoName;
        mCompanyName = companyName;
        mPrice = price;
        mId = id;
        this.code = code;
        mrp = 0;
        scheme = "";
        mstock = stock;
    }

    public Medicine(String mMedicentoName, String mCompanyName, float mPrice, String mId, String code, int mstock, String packing, float mrp, String scheme) {
        this.mMedicentoName = mMedicentoName;
        this.mCompanyName = mCompanyName;
        this.mPrice = mPrice;
        this.mId = mId;
        this.code = code;
        this.mstock = mstock;
        Packing = packing;
        this.mrp = mrp;
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
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

    public float getPrice() {
        return mPrice;
    }

    public String getId() {
        return mId;
    }

    public int getMstock() {
        return mstock;
    }
}
