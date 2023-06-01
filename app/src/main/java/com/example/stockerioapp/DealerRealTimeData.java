package com.example.stockerioapp;

public class DealerRealTimeData {

    String name, phone;

    public DealerRealTimeData() {
    }

    public DealerRealTimeData(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
