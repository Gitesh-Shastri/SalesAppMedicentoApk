package com.salesappmedicento.networking.data;

public class RecentOrder {
    private String pOrderId;
    private String pDate;
    private int Total;
    private String status;

    public RecentOrder(String orderId, String date, int total) {
        pOrderId = orderId;
        pDate = date;
        Total = total;
    }

    public RecentOrder(String pOrderId, String pDate, int total, String status) {
        this.pOrderId = pOrderId;
        this.pDate = pDate;
        Total = total;
        this.status = status;
    }

    public String getpOrderId() {
        return pOrderId;
    }

    public void setpOrderId(String pOrderId) {
        this.pOrderId = pOrderId;
    }

    public String getpDate() {
        return pDate;
    }

    public void setpDate(String pDate) {
        this.pDate = pDate;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return pOrderId;
    }

    public String getDate() {
        return pDate;
    }

    public int getTotal() {
        return Total;
    }
}
