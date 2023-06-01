package com.example.stockerioapp;

public class VendorProductTableDetails {

    String barCode, productName,  productPrice, StockCount, alertCount, dealerPhoneNo;

    public VendorProductTableDetails() {
    }

    public VendorProductTableDetails(String barCode, String productName, String productPrice, String StockCount, String alertCount, String dealerPhoneNo) {
        this.barCode = barCode;
        this.productName = productName;
        this.productPrice = productPrice;
        this.StockCount = StockCount;
        this.alertCount = alertCount;
        this.dealerPhoneNo = dealerPhoneNo;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getproductName() {
        return productName;
    }

    public void setproductName(String productName) {
        this.productName = productName;
    }

    public String getproductPrice() {
        return productPrice;
    }

    public void setproductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getStockCount() {
        return StockCount;
    }

    public void setStockCount(String StockCount) {
        this.StockCount = StockCount;
    }

    public String getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(String alertCount) {
        this.alertCount = alertCount;
    }

    public String getDealerPhoneNo() {
        return dealerPhoneNo;
    }

    public void setDealerPhoneNo(String dealerPhoneNo) {
        this.dealerPhoneNo = dealerPhoneNo;
    }
}
