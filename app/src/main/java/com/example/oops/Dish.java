package com.example.oops;

public class Dish {
    private String name;
    private int imageResId;
    private String price;

    // Constructor
    public Dish(String name, int imageResId, String price) {
        this.name = name;
        this.imageResId = imageResId;
        this.price = price;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getPrice() {
        return price;
    }
}
