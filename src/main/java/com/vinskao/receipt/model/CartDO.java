package com.vinskao.receipt.model;

import java.util.Map;

public class CartDO {
    private Map<String, Map<String, LocationENUM>> carts;

    public CartDO() {
    }

    public CartDO(Map<String,Map<String,LocationENUM>> carts) {
        this.carts = carts;
    }

    public Map<String,Map<String,LocationENUM>> getCarts() {
        return this.carts;
    }

    public void setCarts(Map<String,Map<String,LocationENUM>> carts) {
        this.carts = carts;
    }

    public CartDO carts(Map<String,Map<String,LocationENUM>> carts) {
        setCarts(carts);
        return this;
    }
}
