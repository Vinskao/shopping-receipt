package com.vinskao.receipt.model;

import java.math.BigDecimal;
import java.util.Map;

public class PriceDO {
    private Map<String, BigDecimal> prices;

    public PriceDO() {
    }

    public PriceDO(Map<String,BigDecimal> prices) {
        this.prices = prices;
    }

    public Map<String,BigDecimal> getPrices() {
        return this.prices;
    }

    public void setPrices(Map<String,BigDecimal> prices) {
        this.prices = prices;
    }

    public PriceDO prices(Map<String,BigDecimal> prices) {
        setPrices(prices);
        return this;
    }
}
