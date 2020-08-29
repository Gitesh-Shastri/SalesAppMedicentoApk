package com.salesappmedicento.networking.data;

import java.io.Serializable;

public class OrderedMedicine implements Serializable{
    private String mMedicineName;
    private String mMedicineCompany;
    private int mQty;
    private float mRate;
    private String code;
    private float mCost;
    private int stock;
    private String packing, discount, quantity;

    public OrderedMedicine(){

    }

    public OrderedMedicine(String mMedicineName, String mMedicineCompany, int mQty, float mRate, String code, float mCost, int stock, String packing, String discount, String quantity) {
        this.mMedicineName = mMedicineName;
        this.mMedicineCompany = mMedicineCompany;
        this.mQty = mQty;
        this.mRate = mRate;
        this.code = code;
        this.mCost = mCost;
        this.stock = stock;
        this.packing = packing;
        this.discount = discount;
        this.quantity = quantity;
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public OrderedMedicine(String name, String company, int qty, float rate, float cost, int stock, String code){
        mMedicineName = name;
        mMedicineCompany = company;
        mQty = qty;
        mRate = rate;
        mCost = cost;
        this.code = code;
        this.stock  = stock;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
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

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock() {

        return stock;
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
