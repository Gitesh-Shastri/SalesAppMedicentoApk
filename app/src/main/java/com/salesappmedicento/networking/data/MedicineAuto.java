package com.salesappmedicento.networking.data;

public class MedicineAuto {

    private String name, company, discount, quantity, packing;
    private float mrp;

    public MedicineAuto(String name, String company, String discount, String quantity, String packing, float mrp) {
        this.name = name;
        this.company = company;
        this.discount = discount;
        this.quantity = quantity;
        this.packing = packing;
        this.mrp = mrp;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public MedicineAuto(String name, String company, String discount, String quantity, float mrp) {
        this.name = name;
        this.company = company;
        this.discount = discount;
        this.quantity = quantity;
        this.mrp = mrp;
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

    public MedicineAuto(String name, String company, float mrp) {
        this.name = name;
        this.company = company;
        this.mrp = mrp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }
}
