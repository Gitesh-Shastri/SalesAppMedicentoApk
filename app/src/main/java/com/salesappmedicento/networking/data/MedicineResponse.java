package com.salesappmedicento.networking.data;

public class MedicineResponse {

    private String Item_name, manfc_name, item_code, packing, scheme, discount, offer_qty;
    private int id, qty;
    private float ptr, mrp;

    public MedicineResponse(String item_name, String manfc_name, String item_code, String packing, String scheme, String discount, String offer_qty, int qty, int id, float ptr, float mrp) {
        Item_name = item_name;
        this.manfc_name = manfc_name;
        this.item_code = item_code;
        this.packing = packing;
        this.scheme = scheme;
        this.discount = discount;
        this.offer_qty = offer_qty;
        this.id = id;
        this.ptr = ptr;
        this.mrp = mrp;
        this.qty = qty;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getItem_name() {
        return Item_name;
    }

    public void setItem_name(String item_name) {
        Item_name = item_name;
    }

    public String getManfc_name() {
        return manfc_name;
    }

    public void setManfc_name(String manfc_name) {
        this.manfc_name = manfc_name;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPtr() {
        return ptr;
    }

    public void setPtr(float ptr) {
        this.ptr = ptr;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
    }
}
