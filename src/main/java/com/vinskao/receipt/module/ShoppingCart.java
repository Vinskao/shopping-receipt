package com.vinskao.receipt.module;

import java.math.BigDecimal;
import java.util.Collection;

import com.vinskao.receipt.config.PricesConfigLoader;
import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.PriceDO;

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
     * 用於從 JSON 中取得價格資料的 PriceDO
     */
    private PriceDO priceDO;

    public ShoppingCart() {
        this.taxCalculator = new TaxCalculator();
        // 將 prices.json 讀取成 PriceDO 物件
        this.priceDO = PricesConfigLoader.load();
    }

    /**
     * 計算購物車中所有物品的小計（不含稅）。
     *
     * @param items 購物車中的物品清單，每個物品包含價格與數量資訊
     * @return 返回所有物品價格乘以數量的累計總和
     */
    public BigDecimal calSubtotal(Collection<ItemVO> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemVO item : items) {
            // 嘗試使用 ItemVO 中所帶的價格
            BigDecimal price = item.getPrice();
            
            // 若ItemVO的price為null，則從PriceDO映射中取得對應的價格
            if (price == null) {
                price = priceDO.getPrices().get(item.getProductName());
            }
            
            if (price != null) {
                subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            } else {
                // 若從PriceDO中仍然找不到價格，將印出警告訊息。
                System.out.println("Item 價格為 null: " + item.getProductName());
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

    /**
     * 先全部轉小寫，再將開頭大寫、底鹹轉空格。
     */
    public String itemNameFormatter(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        
        // 將整個字串先全部轉成小寫
        name = name.toLowerCase();
        
        StringBuilder formattedName = new StringBuilder();
        // boolean標誌，表示下一個字符需要轉成大寫
        boolean capitalizeNext = true;
        
        for (int i = 0; i < name.length(); i++) {
            char currentChar = name.charAt(i);
            
            if (currentChar == '_') {
                formattedName.append(' ');
                // 下一個字符轉大寫
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    formattedName.append(Character.toUpperCase(currentChar));
                    // 停止轉大寫
                    capitalizeNext = false;
                } else {
                    formattedName.append(currentChar);
                }
            }
        }
        return formattedName.toString();
    }
}
