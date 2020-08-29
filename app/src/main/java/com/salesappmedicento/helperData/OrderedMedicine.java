package com.salesappmedicento.helperData;

import java.io.Serializable;

public class OrderedMedicine implements Serializable {
    private String mMedicineName;
    private String mMedicineCompany;
    private int mQty;
    private float mRate;
    private String code;
    private float mCost;
    private int stock;
    private String packing;
    private float mrp;
    private String scheme;
    private String discount, offer_qty;

    public String getDiscount() {
        return discount;
    }

    public String getOffer_qty() {
        return offer_qty;
    }

    public OrderedMedicine(String mMedicineName, String mMedicineCompany, int mQty, float mRate, String code, float mCost, int stock, String packing, float mrp, String scheme, String discount, String offer_qty) {
        this.mMedicineName = mMedicineName;
        this.mMedicineCompany = mMedicineCompany;
        this.mQty = mQty;
        this.mRate = mRate;
        this.code = code;
        this.mCost = mCost;
        this.stock = stock;
        this.packing = packing;
        this.mrp = mrp;
        this.scheme = scheme;
        this.discount = discount;
        this.offer_qty = offer_qty;
    }

    public OrderedMedicine(String mMedicineName, String mMedicineCompany, int mQty, float mRate, String code, float mCost, int stock, String packing) {
        this.mMedicineName = mMedicineName;
        this.mMedicineCompany = mMedicineCompany;
        this.mQty = mQty;
        this.mRate = mRate;
        this.code = code;
        this.mCost = mCost;
        this.stock = stock;
        this.packing = packing;
    }

    public OrderedMedicine(String mMedicineName, String mMedicineCompany, int mQty, float mRate, String code, float mCost, int stock, String packing, float mrp, String scheme) {
        this.mMedicineName = mMedicineName;
        this.mMedicineCompany = mMedicineCompany;
        this.mQty = mQty;
        this.mRate = mRate;
        this.code = code;
        this.mCost = mCost;
        this.stock = stock;
        this.packing = packing;
        this.mrp = mrp;
        this.scheme = scheme;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getmMedicineName() {
        return mMedicineName;
    }

    public void setmMedicineName(String mMedicineName) {
        this.mMedicineName = mMedicineName;
    }

    public String getmMedicineCompany() {
        return mMedicineCompany;
    }

    public void setmMedicineCompany(String mMedicineCompany) {
        this.mMedicineCompany = mMedicineCompany;
    }

    public int getmQty() {
        return mQty;
    }

    public void setmQty(int mQty) {
        this.mQty = mQty;
    }

    public float getmRate() {
        return mRate;
    }

    public void setmRate(float mRate) {
        this.mRate = mRate;
    }

    public float getmCost() {
        return mCost;
    }

    public void setmCost(float mCost) {
        this.mCost = mCost;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public OrderedMedicine(){

    }

    public OrderedMedicine(String name, String company, int qty, float rate, float cost, String code){
        mMedicineName = name;
        this.code = code;
        mMedicineCompany = company;
        mQty = qty;
        mRate = rate;
        mCost = cost;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMedicineName() {
        return mMedicineName;
    }

    public String getMedicineCompany() {
        return mMedicineCompany;
    }

    public int getQty() {
        return mQty;
    }

    public float getRate() {
        return mRate;
    }

    public float getCost(){return mCost;}

    public void setMedicineCompany(String company) {
        this.mMedicineCompany = company;
    }

    public void setMedicineName(String name) {
        this.mMedicineName = name;
    }

    public void setQty(int qty) {
        this.mQty = qty;
    }

    public void setRate(float rate) {this.mRate = rate;}

    public void setCost(float cost){
        this.mCost = cost;
    }
}
