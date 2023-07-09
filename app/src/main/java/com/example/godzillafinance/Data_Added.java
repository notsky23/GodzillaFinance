package com.example.godzillafinance;

public class Data_Added {
    String id, item_name,date;
    int amount,month;
    public Data_Added(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Data_Added(String id, String item_name, String date, int month, int amount) {
        this.id = id;
        this.item_name = item_name;
        this.date = date;
        this.month = month;
        this.amount = amount;
    }
}
