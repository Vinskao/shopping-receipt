package com.vinskao.receipt.module;

import java.math.BigDecimal;
import java.util.Collection;

import com.vinskao.receipt.model.ItemVO;

/**
 * ShoppingCart 購物車類別，用於計算購物車中物品的小計、稅金與總金額。
 * @author VinsKao
 */
public class ShoppingCart {
    /**
     * 計算所有購物項目的稅金總和。
     */
    private TaxCalculator taxCalculator;

    /**
     * 計算購物車中所有物品的小計（不含稅）。
     *
     * @param items 購物車中的物品清單，每個物品包含價格與數量資訊
     * @return 返回所有物品價格乘以數量的累計總和
     */
    public BigDecimal calSubtotal(Collection<ItemVO> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemVO item : items) {
            BigDecimal price = item.getPrice();
            
            // Check if price is null and handle it
            if (price != null) {
                subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            } else {
                // Log the issue or handle it as needed, for now we'll skip the item
                // You could also log a warning message or set a default value for price
                System.out.println("Warning: Item price is null for item: " + item.getProductName());
            }
        }
        return subtotal;
    }

    /**
     * 計算購物車中所有物品的總稅金。
     *
     * @param items 購物車中的物品清單
     * @return 返回所有物品的稅金總和
     */
    public BigDecimal calTax(Collection<ItemVO> items) {
        return taxCalculator.calculateTotalTax(items);
    }

    /**
     * 計算購物車中所有物品的稅後總金額。
     *
     * @param items 購物車中的物品清單.
     * @return 返回小計與稅金相加後的總金額
     */
    public BigDecimal calTotal(Collection<ItemVO> items) {
        return calSubtotal(items).add(calTax(items));
    }

    public String itemNameFormatter(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
    
        StringBuilder formattedName = new StringBuilder();
        // Boolean flag indicating whether the next encountered character should be converted to uppercase
        boolean capitalizeNext = true;
    
        for (int i = 0; i < name.length(); i++) {
            char currentChar = name.charAt(i);
    
            if (currentChar == '_') {
                // Replace underscore with a space and set flag so that the next character is capitalized
                formattedName.append(' ');
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    formattedName.append(Character.toUpperCase(currentChar));
                    capitalizeNext = false;
                } else {
                    formattedName.append(currentChar);
                }
            }
        }
        return formattedName.toString();
    }
}
