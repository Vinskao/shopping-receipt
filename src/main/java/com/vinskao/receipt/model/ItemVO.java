package com.vinskao.receipt.model;  

import java.math.BigDecimal;

public class ItemVO {
    private String productName;
    private BigDecimal price;
    private int quantity;
    private LocationENUM location;

    public ItemVO() {
    }

    public ItemVO(String productName, BigDecimal price, int quantity, LocationENUM location) {
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

    public LocationENUM getLocation() {
        return location;
    }

    public void setLocation(LocationENUM location) {
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