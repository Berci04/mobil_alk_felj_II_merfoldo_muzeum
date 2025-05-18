package com.example.myapplication;

public class Ticket {
    private String name;
    private String description;
    private int price;
    private int count;

    public Ticket(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.count = 0;
    }


    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getPrice() {
        return price;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
