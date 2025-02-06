package com.vinskao.receipt.vo;  

import java.math.BigDecimal;

public class Item {
    private String productName;
    private BigDecimal price;
    private int quantity;
    private Location location;

    public Item() {
    }

    public Item(String productName, BigDecimal price, int quantity, Location location) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
    }

    // Getter 與 Setter
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    @Override
    public String toString() {
        return "Item{" +
               "productName='" + productName + '\'' +
               ", price=" + price +
               ", quantity=" + quantity +
               ", location=" + location +
               '}';
    }
} 