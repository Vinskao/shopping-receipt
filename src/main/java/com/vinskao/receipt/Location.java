package com.vinskao.receipt;

import java.math.BigDecimal;

public enum Location {
    CA(new BigDecimal("0.0975"), new String[]{"food"}),
    NY(new BigDecimal("0.08875"), new String[]{"food", "clothing"});

    private final BigDecimal taxRate;
    private final String[] exemptCategories;

    Location(BigDecimal taxRate, String[] exemptCategories) {
        this.taxRate = taxRate;
        this.exemptCategories = exemptCategories;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public String[] getExemptCategories() {
        return exemptCategories;
    }
} 