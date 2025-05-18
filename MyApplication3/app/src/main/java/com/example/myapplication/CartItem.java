package com.example.myapplication;

import com.google.firebase.Timestamp;

public class CartItem {
    private String id;
    private String name;
    private String description;
    private int price;
    private int count;
    private boolean purchased;
    private Timestamp addedAt;
    private Timestamp purchaseDate;

    public CartItem() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getCount() { return count; }
    public boolean isPurchased() { return purchased; }
    public Timestamp getAddedAt() { return addedAt; }
    public Timestamp getPurchaseDate() { return purchaseDate; }

    public void setCount(int count) {
        this.count = count;
    }
}
