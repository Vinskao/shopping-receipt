package com.vinskao.receipt.module;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

import com.vinskao.receipt.config.LocationsConfigLoader;
import com.vinskao.receipt.config.PricesConfigLoader;
import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.LocationDO;
import com.vinskao.receipt.model.PriceDO;

/**
 * TaxCalculator 類別負責根據商品的所在區域與類別資訊計算所需繳納的稅金。
 * 從設定檔中讀取各地區的稅率與免稅產品類別配置，
 * 若屬於免稅產品，則回傳0稅率，否則回傳該區域稅率。
 * 
 * @author VinsKao
 */
public class TaxCalculator {


    /**
     * 儲存各地區之稅率與免稅類別設定。
     * 此設定物件由 {@link LocationsConfigLoader} 讀取數據，
     * 用以判斷各地區適用的稅率與免稅產品類別。
     */
    private LocationDO locationsConfig;
    private PriceDO pricesConfig;

    /**
     * 建構子 TaxCalculator
     * 物件建立時自動從locations.json中載入各地區稅設定，
     * 並將配置資訊儲存於 {@code locationsConfig} 成員變數之中，
     * 以便後續計算稅金時使用。
     */
    public TaxCalculator(){
        this.locationsConfig = LocationsConfigLoader.load();
        this.pricesConfig = PricesConfigLoader.load();
    }
    
    
    /**
     * 根據商品資訊決定並回傳應用的稅率。
     *
     * @param item 傳入欲計算稅金的商品物件，必須包含正確的 {@code Location} 與產品名稱資訊
     * @return 若商品符合免稅條件則回傳 {@link BigDecimal#ZERO}，否則回傳該區域的稅率
     * @throws IllegalArgumentException 當 {@code item} 或其 {@code Location} 為 null 時拋出此異常
     */
    public BigDecimal determineTax(ItemVO item){
        if(item == null || item.getLocation() == null){
            throw new IllegalArgumentException("Item 或 Location 不能為 null");
        }
        
        // 取得商品所在區域的代碼與商品類別
        String location = item.getLocation().name();
        String category = item.getCategory();

        // 從配置中取得該地區的稅率與免稅產品類別列表
        BigDecimal taxRate = locationsConfig.getTaxRate(location);
        List<String> exemptCategories = locationsConfig.getExemptCategories(location);
        
        // DEBUG：印出計算數學
        if (exemptCategories.contains(category)) {
            // System.err.println("Tax: Item '" + item.getProductName() 
            //     + "' location '" + location + "' 免稅'" + category + "'. Tax rate = 0.");
            return BigDecimal.ZERO;
        }
        
        // System.err.println("Tax: Item '" + item.getProductName() 
        //     + "' location '" + location + "' tax rate: " + taxRate + ".");
        return taxRate;        
    }

    /**
     * 計算商品列表中所有商品稅金的總和。
     *
     * @param items 需要計算稅金的商品列表。列表中不可包含 null 值。
     * @return 所有商品稅金的加總。
     * @throws IllegalArgumentException 當 items 為 null 時或任何商品為 null 時拋出此異常。
     */
    public BigDecimal calculateTotalTax(Collection<ItemVO> items) {
        if (items == null) {
            throw new IllegalArgumentException("商品列表不能為 null");
        }
        
        BigDecimal tax = items.stream()
                    .map(item -> {
                        if (item == null) { 
                            throw new IllegalArgumentException("商品列表中包含 null 項目");
                        }
                        // 若該項目的價格為 null，嘗試從 pricesConfig 裡取得對應價格
                        if (item.getPrice() == null) {
                            BigDecimal configPrice = pricesConfig.getPrices()
                                    .getOrDefault(item.getProductName(), BigDecimal.ZERO);
                            item.setPrice(configPrice);
                        }
                        // 根據當前商品決定適用的稅（如果商品免稅，會是 0）
                        BigDecimal taxRate = determineTax(item);
                        // 計算該商品總價 (總價 = 單價 * 數量)
                        BigDecimal itemTotal = item.getPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                        // 計算該商品需支付的稅額（稅額 = 總價 * 稅率）
                        BigDecimal taxAmount = itemTotal.multiply(taxRate);

                        // DEBUG：印出計算數學
                        // if(taxRate.compareTo(BigDecimal.ZERO) == 0){
                        //     System.err.println("Tax: Item '" + item.getProductName() 
                        //             + "' 免稅. (" + item.getPrice() + " * " 
                        //             + item.getQuantity() + " = " + itemTotal + ") * " 
                        //             + taxRate + " = " + taxAmount);
                        // } else {
                        //     System.err.println("Tax: " 
                        //             + item.getProductName() + "': (" + item.getPrice() + " * " 
                        //             + item.getQuantity() + " = " + itemTotal + ") * " 
                        //             + taxRate + " = " + taxAmount);
                        // }
                        return taxAmount;
                    })
                    // 將所有計算出來的稅額相加， 從 0 起始累加每一次 map 中的 return
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Round up to nearest 0.05
        BigDecimal roundedTax = tax.setScale(1, RoundingMode.HALF_UP);
        if (tax.subtract(roundedTax).compareTo(BigDecimal.ZERO) > 0) {
            roundedTax = roundedTax.add(new BigDecimal("0.05"));
        }
        
        return roundedTax;
    }
}
